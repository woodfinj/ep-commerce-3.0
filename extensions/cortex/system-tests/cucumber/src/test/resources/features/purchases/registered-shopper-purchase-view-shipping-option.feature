@shipments @purchases @headerAuth
Feature: Registered Shopper view shipping option selected for purchase
  As a registered shopper I want to see the shipping option I selected for my purchase so that I can track my order

  Background:
    Given I login as a registered shopper

  Scenario: View shipment cost and options
    When I add item with code portable_tv_hdbuy_sku to my cart
    And I add item with code bundle_with_physical_and_multisku_items_bundle_sku to my cart
    And I select shipping option CanadaPostExpress
    When I make a purchase
    And I navigate to shipment
    And I follow the shipping option link
    Then I see shipping Canada Post Express carrier information Canada Post
    And the cost fields has amount: 47.30, currency: CAD and display: $47.30
    And I can follow a link back to the shipment

  Scenario: View shipment cost and options when free shipping
    When I add item Moto X to the cart
    And I select shipping option FixedPriceWith100PercentOffPromoShippingOption
    When I make a purchase
    And I navigate to shipment
    And I follow the shipping option link
    Then I see the cost field has amount: 0.00, currency: CAD and display: $0.00
