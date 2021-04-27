package q.rest.product.model.archived;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name="prd_make_category")
public class CategoryMake implements Serializable {

    @Id
    @Column(name="make_id")
    private long makeId;

    @Id
    @Column(name="category_id")
    private int categoryId;

    public long getMakeId() {
        return makeId;
    }

    public void setMakeId(long makeId) {
        this.makeId = makeId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public static class CategoryMakePK implements Serializable{

        private static final long serialVersionUID = 1L;
        protected long makeId;
        protected int categoryId;


        public long getMakeId() {
            return makeId;
        }

        public void setMakeId(long makeId) {
            this.makeId = makeId;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CategoryMakePK that = (CategoryMakePK) o;
            return makeId == that.makeId &&
                    categoryId == that.categoryId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(makeId, categoryId);
        }
    }

}
