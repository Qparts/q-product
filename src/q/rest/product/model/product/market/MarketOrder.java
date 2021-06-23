package q.rest.product.model.product.market;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name="prd_market_order")
public class MarketOrder implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_market_order_id_seq_gen", sequenceName = "prd_market_order_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_market_order_id_seq_gen")
    private long id;
    private int companyId;
    private char status;//I = initiated, P = paid
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    private int createdBy;
    private int salesId;
    private int addressCountryId;
    private int addressRegionId;
    private int addressCityId;
    private String  addressPersonName;
    private String addressMobile;
    private String addressLine;
    private double shippingCost;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<MarketOrderItem> items = new ArrayList<>();


    public MarketOrder() {
    }

    public MarketOrder(MarketOrderRequest marketRequest) {
        this.companyId = marketRequest.getCompanyId();
        this.status = 'I';
        this.created = new Date();
        this.createdBy = marketRequest.getSubscriberId();
        this.addressCityId = marketRequest.getAddressCityId();
        this.addressCountryId = marketRequest.getAddressCountryId();
        this.addressRegionId = marketRequest.getAddressRegionId();
        this.addressPersonName = marketRequest.getAddressPersonName();
        this.addressMobile = marketRequest.getMobile();
        this.addressLine = marketRequest.getAddressLine();
        this.shippingCost = marketRequest.getShippingCost();
        for(var item : marketRequest.getItems()){
            MarketOrderItem newItem = new MarketOrderItem();
            newItem.setMarketProductId(item.getMarketProductId());
            newItem.setQuantity(item.getQuantity());
            this.items.add(newItem);
        }
    }

    @JsonIgnore
    public double getItemsSalesPrice(){
        double total = 0;
        for(var item : this.items){
            total += item.getSalesPrice() * item.getQuantity();
        }
        return total;
    }

    public List<MarketOrderItem> getItems() {
        return items;
    }

    public void setItems(List<MarketOrderItem> items) {
        this.items = items;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getSalesId() {
        return salesId;
    }

    public void setSalesId(int salesId) {
        this.salesId = salesId;
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

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }
}
