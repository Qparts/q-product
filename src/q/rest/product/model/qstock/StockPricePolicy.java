package q.rest.product.model.qstock;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "prd_stk_policy")
public class StockPricePolicy implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_stk_policy_id_seq_gen", sequenceName = "prd_stk_policy_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_stk_policy_id_seq_gen")
    private int id;
    @JsonIgnore
    private int companyId;
    private String policyName;
    private double retailFactor;
    private double wholesalesFactor;
    private double specialFactor;
    private boolean defaultPolicy;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public double getRetailFactor() {
        return retailFactor;
    }

    public void setRetailFactor(double retailFactor) {
        this.retailFactor = retailFactor;
    }

    public double getWholesalesFactor() {
        return wholesalesFactor;
    }

    public void setWholesalesFactor(double wholesalesFactor) {
        this.wholesalesFactor = wholesalesFactor;
    }

    public double getSpecialFactor() {
        return specialFactor;
    }

    public void setSpecialFactor(double specialFactor) {
        this.specialFactor = specialFactor;
    }

    public boolean isDefaultPolicy() {
        return defaultPolicy;
    }

    public void setDefaultPolicy(boolean defaultPolicy) {
        this.defaultPolicy = defaultPolicy;
    }
}
