package q.rest.product.model.contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StockDeduct {
    private long cartProductId;
    private long productId;
    private int quantity;
    private int createdBy;
    private List<Map<String,Object>> purchaseProductIds = new ArrayList<>();

    public List<Map<String, Object>> getPurchaseProductIds() {
        return purchaseProductIds;
    }

    public void setPurchaseProductIds(List<Map<String, Object>> purchaseProductIds) {
        this.purchaseProductIds = purchaseProductIds;
    }

    public long getCartProductId() {
        return cartProductId;
    }

    public void setCartProductId(long cartProductId) {
        this.cartProductId = cartProductId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockDeduct that = (StockDeduct) o;
        return productId == that.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}
