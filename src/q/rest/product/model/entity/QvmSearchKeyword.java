package q.rest.product.model.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Table(name = "prd_qvm_search_keywords")
@Entity
@IdClass(QvmSearchKeyword.SearchKeywordPK.class)
public class QvmSearchKeyword implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "vendor_user_id")
    private int vendorUserId;

    @Id
    @Column(name="created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "query")
    private String query;

    @Column(name = "vendor_id")
    private int vendorId;

    @Column(name="app_code")
    private int appCode;

    public int getAppCode() {
        return appCode;
    }

    public void setAppCode(int appCode) {
        this.appCode = appCode;
    }

    public int getVendorUserId() {
        return vendorUserId;
    }

    public void setVendorUserId(int vendorUserId) {
        this.vendorUserId = vendorUserId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public static class SearchKeywordPK implements Serializable{

        private static final long serialVersionUID = 1L;
        protected int vendorUserId;
        protected Date created;
        public SearchKeywordPK() {}

        public int getVendorUserId() {
            return vendorUserId;
        }

        public void setVendorUserId(int vendorUserId) {
            this.vendorUserId = vendorUserId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SearchKeywordPK that = (SearchKeywordPK) o;
            return vendorUserId == that.vendorUserId &&
                    Objects.equals(created, that.created);
        }

        @Override
        public int hashCode() {
            return Objects.hash(vendorUserId, created);
        }

        public Date getCreated() {
            return created;
        }

        public void setCreated(Date created) {
            this.created = created;
        }
    }
}
