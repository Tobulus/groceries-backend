package rmbr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@MappedSuperclass
public abstract class IdEntity implements Persistable<Long> {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    @Transient
    public boolean isNew() {
        return null == getId();
    }

    @Override
    public String toString() {
        return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
    }

    // TODO: once we override this method, we cannot use getOne() from the jpa repositories anymore because of some lazyinit exception
    /*@Override
    public boolean equals(Object obj) {

        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof IdEntity)) {
            return false;
        }

        IdEntity that = (IdEntity) obj;

        return null != this.getId() && this.getId().equals(that.getId());
    }*/

    // TODO: there is a lot of controversy about overriding equals/hashcode/toString for entities -> check
    @Override
    public int hashCode() {

        int hashCode = 17;

        hashCode += null == getId() ? 0 : getId().hashCode() * 31;

        return hashCode;
    }
}
