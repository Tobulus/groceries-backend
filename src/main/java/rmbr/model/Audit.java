package rmbr.model;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Audit {

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private Long createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    private Long modifiedDate;

    @Column(name = "created_by")
    @CreatedBy
    private Long createdBy;

    @Column(name = "modified_by")
    @LastModifiedBy
    private Long modifiedBy;

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
