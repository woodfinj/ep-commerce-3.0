/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.mutable.MutableLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.performancetools.queryanalyzer.beans.JPAQuery;
import com.elasticpath.performancetools.queryanalyzer.beans.Operation;
import com.elasticpath.performancetools.queryanalyzer.beans.QueryStatistics;
import com.elasticpath.performancetools.queryanalyzer.beans.SQLQuery;
import com.elasticpath.performancetools.queryanalyzer.parser.ParserChain;
import com.elasticpath.performancetools.queryanalyzer.parser.chain.UnableToSerializeStatisticsException;
import com.elasticpath.performancetools.queryanalyzer.utils.CollectionUtils;
import com.elasticpath.performancetools.queryanalyzer.utils.Patterns;
import com.elasticpath.performancetools.queryanalyzer.utils.Utils;

/**
 * Cortex log parser used for producing db query statistics based on TRACE log lines generated by OpenJPA,
 * AnnotatedMethodDispatcher and ResourceLinker classes.
 * <p>
 * The final output is serialized into JSON format and depending on the presence of -Dprint.json.to.console.only system property,
 * it will be redirected either to a file (specified with -Doutput.json.file.path or to a default location [USER_HOME/ep/db_statistics.json])
 * or console.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public enum LogParser {

	/**
	 * Log parser instance.
	 */
	INSTANCE;

	private static final Logger LOG = LoggerFactory.getLogger(LogParser.class);
	private static final long MINIMUM_OPERATION_DURATION_THRESHOLD_MS = 2L;

	private final ObjectMapper mapper = new ObjectMapper();
	private final File outputJSONFile;

	/**
	 * Default constructor used for Jackson configuration as well as obtaining the reference to an
	 * output JSON file.
	 */
	LogParser() {
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		mapper.setDateFormat(new SimpleDateFormat(Utils.DATE_FORMAT_PATTERN, Locale.getDefault()));

		outputJSONFile = Utils.getOutputJSONFileIfEnabled();
	}

	/**
	 * Main method.
	 *
	 * @param args parameters;
	 */
	public static void main(final String[] args) {
		try {
			final File cortexLogFile = QueryAnalyzerConfigurator.INSTANCE
					.setLogFileFromLogbackConfiguration()
					.getLogFile();

			final LogParser logParser = LogParser.INSTANCE;

			final QueryStatistics statistics = logParser.parse(cortexLogFile);
			logParser.generateStatistics(statistics);
		} catch (Exception e) {
			LOG.error("Error occurred", e);
		}
	}

	/**
	 * Parses log file.
	 *
	 * @param inputLogFile Cortex log file to be parsed.
	 * @return the {@link QueryStatistics} instance.
	 * @throws Exception an exception.
	 */
	public QueryStatistics parse(final File inputLogFile) throws Exception {
		final File[] logFiles = getLogFiles(inputLogFile);

		final QueryStatistics statistics = new QueryStatistics();

		for (File logFile : logFiles) {
			LOG.debug("Parsing log file {}", logFile);

			String line;

			try (BufferedReader reader = getBufferedReader(logFile)) {
				ParserChain parserChain = new ParserChain(statistics, reader);
				while ((line = reader.readLine()) != null) {
					parserChain.parse(line);
				}
			}
		}

		return statistics;
	}

	private File[] getLogFiles(final File logFileToParse) {
		final String logFileNamePrefix = logFileToParse.getName().split("[.]")[0];
		final File logFolder = logFileToParse.getParentFile();

		//there could be more than one log file with db statistics - we need them all
		return logFolder.listFiles((file, fileName) -> fileName.startsWith(logFileNamePrefix));
	}

	private BufferedReader getBufferedReader(final File logFileToParse) throws Exception {
		return Files.newBufferedReader(logFileToParse.toPath());
	}

	/**
	 * Generates db statistics and saves to JSON format.
	 *
	 * @param statistics the {@link QueryStatistics} instance.
	 * @throws Exception an exception.
	 */
	public void generateStatistics(final QueryStatistics statistics) throws Exception {
		final Map<String, Integer> totalDBCallsPerTable = statistics.getTotalDBCallsPerTable();
		final Map<String, Integer> totalJPACallsPerEntity = statistics.getTotalJPACallsPerEntity();

		final Map<String, Integer> totalDBCallsPerOperation = statistics.getTotalDBCallsPerOperation();
		final Map<String, Integer> totalJPACallsPerOperation = statistics.getTotalJPACallsPerOperation();

		final Map<String, Integer> totalDBCallExeTimePerOperation = statistics.getTotalDBCallExeTimePerOperationMs();
		final Map<Long, Integer> totalOperationsPerDuration = statistics.getTotalOperationsPerDuration();

		final List<Operation> operations = statistics.getOperations();

		statistics.setTotalNumberOfOperations(operations.size());

		MutableLong totalExecutionTime = new MutableLong();

		for (Operation operation : operations) {
			if (!(operation.getTotalOpDurationMs() < MINIMUM_OPERATION_DURATION_THRESHOLD_MS && operation.getJpaQueries().isEmpty())) {

				if (operation.getJpaQueries().isEmpty()) {
					statistics.addOperationWithoutJPA(operation);
				} else {
					statistics.addOperationWithJPA(operation);
				}

				totalOperationsPerDuration.merge(operation.getTotalOpDurationMs(), 1, (op1, op2) -> op1 + op2);
				totalExecutionTime.add(operation.getTotalOpDurationMs());

				final Map<String, Integer> totalDBCallsPerTablePerOperation = operation.getTotalDBCallsPerTable();
				final Map<String, Integer> totalJPACallsPerEntityPerOperation = operation.getTotalJPACallsPerEntity();

				String operationURI = operation.getUri();
				int totalDBCallExeTime = 0;

				for (JPAQuery jpaQuery : operation.getJpaQueries()) {
					CollectionUtils.updateTotalCallsPerOperation(Patterns.JPA_ENTITY_PATTERN.matcher(jpaQuery.getQuery()),
							totalJPACallsPerEntityPerOperation,
							totalJPACallsPerEntity);
					for (SQLQuery sqlQuery : jpaQuery.getSqlQueries()) {
						CollectionUtils.updateTotalCallsPerOperation(Patterns.TABLE_PATTERN.matcher(sqlQuery.getQuery()),
								totalDBCallsPerTablePerOperation,
								totalDBCallsPerTable);
						totalDBCallExeTime += sqlQuery.getExeTimeMs();
					}
				}

				int sum = totalDBCallsPerTablePerOperation.values().stream().mapToInt(Integer::intValue).sum();

				if (sum > 0) {
					totalDBCallsPerOperation.put(operationURI, sum);
				}
				sum = totalJPACallsPerEntityPerOperation.values().stream().mapToInt(Integer::intValue).sum();
				if (sum > 0) {
					totalJPACallsPerOperation.put(operationURI, sum);
				}
				if (totalDBCallExeTime > 0) {
					totalDBCallExeTimePerOperation.put(operationURI, totalDBCallExeTime);
				}
			}
		}

		sortResults(totalDBCallsPerTable, totalJPACallsPerEntity, totalDBCallsPerOperation,
				totalJPACallsPerOperation, totalDBCallExeTimePerOperation);

		populateStatistics(statistics, totalDBCallsPerTable, totalJPACallsPerEntity,
				totalDBCallExeTimePerOperation, totalExecutionTime.longValue());

		serializeStatisticsToJSON(statistics);
	}

	private void sortResults(final Map<String, Integer> totalDBCallsPerTable,
							 final Map<String, Integer> totalJPACallsPerEntity,
							 final Map<String, Integer> totalDBCallsPerOperation,
							 final Map<String, Integer> totalJPACallsPerOperation,
							 final Map<String, Integer> totalDBCallExeTimePerOperation) {

		final Comparator<Map.Entry<String, Integer>> mapEntryComparator = Map.Entry.comparingByValue();

		CollectionUtils.sortMapEntries(totalDBCallsPerOperation, mapEntryComparator);
		CollectionUtils.sortMapEntries(totalJPACallsPerOperation, mapEntryComparator);
		CollectionUtils.sortMapEntries(totalDBCallExeTimePerOperation, mapEntryComparator);

		CollectionUtils.sortMapEntries(totalDBCallsPerTable, mapEntryComparator);
		CollectionUtils.sortMapEntries(totalJPACallsPerEntity, mapEntryComparator);
	}

	private void populateStatistics(final QueryStatistics statistics,
									final Map<String, Integer> totalDBCallsPerTable,
									final Map<String, Integer> totalJPACallsPerEntity,
									final Map<String, Integer> totalDBCallExeTimePerOperation,
									final long totalExecutionTime) {
		statistics.setOverallDBCalls(totalDBCallsPerTable.values().stream().mapToInt(Integer::intValue).sum());
		statistics.setOverallJPACalls(totalJPACallsPerEntity.values().stream().mapToInt(Integer::intValue).sum());
		statistics.setOverallDbExeTimeMs(totalDBCallExeTimePerOperation.values().stream().mapToInt(Integer::intValue).sum());

		statistics.getOperationsWithJPADesc().sort((op1, op2) -> op2.getTotalOpDurationMs().compareTo(op1.getTotalOpDurationMs()));

		statistics.setTotalExecutionTime(totalExecutionTime);
		statistics.setTotalNumberOfOperationsWithJPA(statistics.getOperationsWithJPADesc().size());
		statistics.setTotalNumberOfOperationsWithoutJPA(statistics.getOperationsWithoutJPA().size());
	}

	private void serializeStatisticsToJSON(final QueryStatistics statistics) {
		final boolean outputJSONFileExists = outputJSONFile != null;

		try (Writer writer = outputJSONFileExists ? Files.newBufferedWriter(outputJSONFile.toPath(), StandardCharsets.UTF_8)
				: new OutputStreamWriter(System.out, StandardCharsets.UTF_8)) {

			mapper.writeValue(writer, statistics);

			if (outputJSONFileExists) {
				LOG.debug("DB statistics saved @ {}", outputJSONFile);
			}
		} catch (IOException ex) {
			throw new UnableToSerializeStatisticsException(ex);
		}
	}
}
