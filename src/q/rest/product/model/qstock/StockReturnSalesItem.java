package q.rest.product.model.qstock;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "prd_stk_sales_return_item")
@Entity
public class StockReturnSalesItem implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_stk_sales_return_item_id_seq_gen", sequenceName = "prd_stk_sales_return_item_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_stk_sales_return_item_id_seq_gen")
    private int id;
    @Column(name="sales_return_id")
    private int salesReturnId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="sales_item_id")
    private StockSalesItem salesItem;
    private int quantity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSalesReturnId() {
        return salesReturnId;
    }

    public void setSalesReturnId(int salesReturnId) {
        this.salesReturnId = salesReturnId;
    }

    public StockSalesItem getSalesItem() {
        return salesItem;
    }

    public void setSalesItem(StockSalesItem salesItem) {
        this.salesItem = salesItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
