package q.rest.product.model.entity.v3.stock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;
import q.rest.product.model.contract.v3.OfferHolder;
import q.rest.product.model.contract.v3.StockHolder;
import q.rest.product.model.contract.v3.UploadHolder;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="prd_company_product")
public class CompanyProduct {
    @Id
    @SequenceGenerator(name = "prd_company_product_id_seq_gen", sequenceName = "prd_company_product_id_seq", initialValue=1000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_company_product_id_seq_gen")
    private long id;
    private int companyId;
    private String partNumber;
    private String alternativeNumber;
    private int productId;
    private String brandName;
    private double retailPrice;
    private double wholesalesPrice;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "company_product_id")
    //@Where(clause = "id not in (select q.company_product_id from prd_company_stock_offer q where now() between q.offer_start_date and q.offer_end_date and q.branch_id = branch_id)")
    private Set<CompanyStock> stock = new HashSet<>();
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "company_product_id")
    @Where(clause = "now() between offer_start_date and offer_end_date")
    private Set<CompanyStockOffer> offers = new HashSet<>();

    public CompanyProduct(){

    }

    //used to create a new one after stock upload
    public CompanyProduct(StockHolder stockVar, UploadHolder holder){
        this.companyId = holder.getCompanyId();
        this.partNumber = stockVar.getPartNumber();
        this.alternativeNumber = stockVar.getAlternativeNumber();
        this.brandName = stockVar.getBrand();
        this.retailPrice = stockVar.getRetailPrice();
        this.wholesalesPrice = stockVar.getWholesalesPrice();
        this.created = holder.getDate();
        CompanyStock newStock = new CompanyStock(stockVar, holder);
        this.stock.add(newStock);
    }


    //used to create a new one after stock upload
    public CompanyProduct(OfferHolder offerVar, UploadHolder holder, CompanyOfferUploadRequest req){
        this.companyId = holder.getCompanyId();
        this.partNumber = offerVar.getPartNumber();
        this.alternativeNumber = offerVar.getAlternativeNumber();
        this.brandName = offerVar.getBrand();
        this.retailPrice = 0;
        this.wholesalesPrice = 0;
        this.created = holder.getDate();
        CompanyStock newStock = new CompanyStock(offerVar, holder);
        this.stock.add(newStock);
        addNewOffer(offerVar, holder.getDate(), req);
    }


//used to update existing one after stock upload
    @JsonIgnore
    public void updateAfterUploadStock(StockHolder stockVar, UploadHolder holder){
        this.retailPrice = stockVar.getRetailPrice();
        this.wholesalesPrice = stockVar.getWholesalesPrice();
        if((this.alternativeNumber == null || this.alternativeNumber.length() == 0) && stockVar.getAlternativeNumber() != null){
            this.alternativeNumber = stockVar.getAlternativeNumber();
        }
        //availability
        CompanyStock cs = this.getStockFromBranchId(holder.getBranchId());
        if(cs == null){
            CompanyStock newStock = new CompanyStock(stockVar, holder);
            this.stock.add(newStock);
        }
        else{
            //update found stock
            cs.setQuantity(stockVar.getQuantity());
            cs.setCreated(holder.getDate());
            cs.setOfferOnly(false);
        }
    }


    //used to update existing one after offer upload
    @JsonIgnore
    public void updateAfterUploadOffer(OfferHolder offerVar, UploadHolder holder, CompanyOfferUploadRequest req){
        //availability
        if((this.alternativeNumber == null || this.alternativeNumber.length() == 0) && offerVar.getAlternativeNumber() != null){
            this.alternativeNumber = offerVar.getAlternativeNumber();
        }
        if(stock.isEmpty()){
            //create temp stock (offer only = true)
            CompanyStock cs = new CompanyStock(offerVar, holder);
            this.stock.add(cs);
        }
        addNewOffer(offerVar, holder.getDate(), req);
    }

    private void addNewOffer(OfferHolder offerVar, Date date, CompanyOfferUploadRequest req){
        CompanyStockOffer offer = new CompanyStockOffer();
        offer.setQuantity(offerVar.getQuantity());
        offer.setOfferPrice(offerVar.getOfferPrice());
        offer.setOfferRequestId(req.getId());
        offer.setOfferStartDate(req.getStartDate());
        offer.setOfferEndDate(req.getEndDate());
        offer.setCreated(date);
        this.offers.add(offer);
    }


    @JsonIgnore
    public CompanyStock getStockFromBranchId(int id){
        for(CompanyStock cs : stock){
            if(cs.getBranchId() == id){
                return cs;
            }
        }
        return null;
    }

    public Set<CompanyStockOffer> getOffers() {
        return offers;
    }

    public void setOffers(Set<CompanyStockOffer> offers) {
        this.offers = offers;
    }

    public Set<CompanyStock> getStock() {
        return stock;
    }

    public void setStock(Set<CompanyStock> stock) {
        this.stock = stock;
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

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getAlternativeNumber() {
        return alternativeNumber;
    }

    public void setAlternativeNumber(String alternativeNumber) {
        this.alternativeNumber = alternativeNumber;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public double getWholesalesPrice() {
        return wholesalesPrice;
    }

    public void setWholesalesPrice(double wholesalesPrice) {
        this.wholesalesPrice = wholesalesPrice;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
