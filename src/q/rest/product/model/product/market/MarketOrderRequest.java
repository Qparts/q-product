package q.rest.product.model.product.market;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.*;

public class MarketOrderRequest {
    private int addressCountryId;
    private int addressRegionId;
    private int addressCityId;
    private String  addressPersonName;
    private String addressMobile;
    private String addressLine;
    private List<MarketOrderItem> items = new ArrayList<>();
    private char paymentMethod;// C = card, W = wire transfer
    private double promoDiscount;
    private double vatPercentage;
    private int countryId;
    private String description;
    private String country;
    private String firstName;
    private String lastName;
    private String email;
    private String countryCode;
    private String mobile;
    private String clientIp;
    private String extension;
    private String mimeType;
    private int companyId;
    private int subscriberId;
    private double shippingCost;


    public Map<String,Object> getPaymentRequestObject(long orderId, double itemsAmount){
        Map<String,Object> map = new HashMap<>();
        map.put("salesType", 'M');
        map.put("paymentMethod", this.paymentMethod);
        map.put("vatPercentage", this.vatPercentage);
        map.put("clientIp", this.clientIp);
        map.put("countryId", this.countryId);
        map.put("country", this.country);
        map.put("countryCode", this.countryCode);
        map.put("companyId", companyId);
        map.put("subscriberId", subscriberId);
        map.put("firstName", this.firstName);
        map.put("lastName", this.lastName);
        map.put("email", this.email);
        map.put("mobile", this.mobile);
        map.put("description", "Market Order: " + orderId);
        map.put("marketOrderId", orderId);
        map.put("baseAmount", itemsAmount + shippingCost);
    //    private String extension;
      //  private String mimeType;
        return map;
    }

    public int getAddressCountryId() {
        return addressCountryId;
    }

    public void setAddressCountryId(int addressCountryId) {
        this.addressCountryId = addressCountryId;
    }

    public int getAddressRegionId() {
        return addressRegionId;
    }

    public void setAddressRegionId(int addressRegionId) {
        this.addressRegionId = addressRegionId;
    }

    public int getAddressCityId() {
        return addressCityId;
    }

    public void setAddressCityId(int addressCityId) {
        this.addressCityId = addressCityId;
    }

    public String getAddressPersonName() {
        return addressPersonName;
    }

    public void setAddressPersonName(String addressPersonName) {
        this.addressPersonName = addressPersonName;
    }

    public String getAddressMobile() {
        return addressMobile;
    }

    public void setAddressMobile(String addressMobile) {
        this.addressMobile = addressMobile;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public List<MarketOrderItem> getItems() {
        return items;
    }

    public void setItems(List<MarketOrderItem> items) {
        this.items = items;
    }

    public char getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(char paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getPromoDiscount() {
        return promoDiscount;
    }

    public void setPromoDiscount(double promoDiscount) {
        this.promoDiscount = promoDiscount;
    }

    public double getVatPercentage() {
        return vatPercentage;
    }

    public void setVatPercentage(double vatPercentage) {
        this.vatPercentage = vatPercentage;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }
}
