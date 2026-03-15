/*
 *	Mark's Code Fractal CFLib DbTest 3.1 Database Test and Prototyping
 *	
 *	Copyright 2016-2026 Mark Stephen Sobkow
 *
 *	CFLib DbTest was used to exercise and prototype the use of the various database types
 *	in CFLib DbUtil, and to prototype the multi-JPA repository framework needed for the
 *	Code Fractal vision of how code should be structured.
 *
 *	These files are part of Mark's Code Fractal CFLib DbTest.
 *
 *	Mark's Code Fractal CFLib DbTest is available under dual commercial license from
 *	Mark Stephen Sobkow, or under the terms of the GNU Library General Public License,
 *	Version 3 or later with static linking exception.
 *
 *	As a special exception, Mark Sobkow gives you permission to link this library
 *	with independent modules to produce an executable, provided that none of them
 *	conflict with the intent of the LGPLv3; that is, you are not allowed to invoke
 *	the methods of this library from non-LGPLv3-compatibly licensed code.  That said,
 *	code which does not rely on this library is free to specify whatever license its
 *	authors decide to use. Mark Sobkow specifically rejects the infectious nature of
 *	the LGPLv3, and considers the mere act of including LGPLv3 modules in an
 *	executable to be perfectly reasonable given tools like modern Java's single-jar
 *	deployment options.
 *
 *	Mark's Code Fractal CFLib DbTest is free software: you can redistribute it and/or
 *	modify it under the terms of the GNU Library General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	Mark's Code Fractal CFLib DbTest is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU Library General Public License for more details.
 *
 *	You should have received a copy of the GNU Library General Public License
 *	along with Mark's Code Fractal CFLib DbTest.  If not, see &lt;https://www.gnu.org/licenses/&gt;.
 *
 *	If you wish to modify and use this code without publishing your changes in order to
 *	tie it to proprietary code, please contact Mark Stephen Sobkow
 *	for a commercial license at mark.sobkow@gmail.com
 */
package server.markhome.mcf.v3_1.cflib.dbtest.appdb;

import jakarta.persistence.*;

import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import server.markhome.mcf.v3_1.cflib.CFLibArgumentOverflowException;
import server.markhome.mcf.v3_1.cflib.CFLibNullArgumentException;
import server.markhome.mcf.v3_1.cflib.CFLibUnresolvedRelationException;
import server.markhome.mcf.v3_1.cflib.dbtest.secdb.SecDbUser;
import server.markhome.mcf.v3_1.cflib.dbtest.secdb.SecDbUserService;
import server.markhome.mcf.v3_1.cflib.dbutil.CFLibDbKeyHash256;
import server.markhome.mcf.v3_1.cflib.inz.Inz;

import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Table(name = "app_addr", schema = "appdb",
    indexes = {
        @Index(name = "app_addr_pidx", columnList = "pid", unique = true),
        @Index(name = "app_addr_axname", columnList = "refuid,addrname", unique = true),
    }
)
@PersistenceContext(unitName = "AppDbPU")
public class AppDbAddress implements Comparable<Object> {
    public static final int ADDR_NAME = 24;
    public static final int ADDR_CONTACT = 64;
    public static final int ADDR_APARTMENT = 16;
    public static final int ADDR_STREET = 64;
    public static final int ADDR_STREET2 = 64;
    public static final int ADDR_CITY = 64;
    public static final int ADDR_PROVINCE = 32;
    public static final int ADDR_COUNTRY = 32;
    public static final int ADDR_POSTAL_CODE = 16;

    @Id
    @AttributeOverrides({
        @AttributeOverride(name = "bytes", column = @Column(name = "pid", nullable = false, unique = true, length = CFLibDbKeyHash256.HASH_LENGTH))
    })
    private CFLibDbKeyHash256 pid;

    @AttributeOverrides({
        @AttributeOverride(name = "bytes", column = @Column(name = "refuid", nullable = false, unique = false, length = CFLibDbKeyHash256.HASH_LENGTH))
    })
    private CFLibDbKeyHash256 refUID;

    @Column(name = "addrname", nullable = false, unique = true, length = ADDR_NAME)
    private String addressName;

    @Column(name = "addrcontact", nullable = true, unique = false, length = ADDR_CONTACT)
    private String addressContact;

    @Column(name = "addrapt", nullable = true, unique = false, length = ADDR_APARTMENT)
    private String addressApartment;

    @Column(name = "addrstreet", nullable = true, unique = false, length = ADDR_STREET)
    private String addressStreet;

    @Column(name = "addrstreet2", nullable = true, unique = false, length = ADDR_STREET2)
    private String addressStreet2;

    @Column(name = "addrcity", nullable = true, unique = false, length = ADDR_CITY)
    private String addressCity;

    @Column(name = "addrprovince", nullable = true, unique = false, length = ADDR_PROVINCE)
    private String addressProvince;

    @Column(name = "addrcountry", nullable = true, unique = false, length = ADDR_COUNTRY)
    private String addressCountry;

    @Column(name = "addrpostalcode", nullable = true, unique = false, length = ADDR_POSTAL_CODE)
    private String addressPostalCode;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @AttributeOverrides({
        @AttributeOverride(name = "bytes", column = @Column(name = "created_by", nullable = false, unique = false, length = CFLibDbKeyHash256.HASH_LENGTH))
    })
    private CFLibDbKeyHash256 createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private java.time.LocalDateTime updatedAt;

    @AttributeOverrides({
        @AttributeOverride(name = "bytes", column = @Column(name = "updated_by", nullable = false, unique = false, length = CFLibDbKeyHash256.HASH_LENGTH))
    })
    private CFLibDbKeyHash256 updatedBy;

    @Transient
    @Autowired
    private transient SecDbUserService secDbUserService;

    public AppDbAddress() {}

    public AppDbAddress(CFLibDbKeyHash256 pid) {
        this.pid = pid;
        this.refUID = null;
        this.addressName = "NameInSetOfAddresses";
        this.addressContact = null;
        this.addressApartment = null;
        this.addressStreet = null;
        this.addressStreet2 = null;
        this.addressCity = null;
        this.addressProvince = null;
        this.addressCountry = null;
        this.addressPostalCode = null;
        this.createdAt = LocalDateTime.now();
        this.createdBy = null;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = null;
    }

    public AppDbAddress(CFLibDbKeyHash256 pid, CFLibDbKeyHash256 refUID, String addressName) {
        this.pid = pid;
        this.refUID = refUID;
        this.addressName = addressName;
        this.addressContact = null;
        this.addressApartment = null;
        this.addressStreet = null;
        this.addressStreet2 = null;
        this.addressCity = null;
        this.addressProvince = null;
        this.addressCountry = null;
        this.addressPostalCode = null;
        this.createdAt = LocalDateTime.now();
        this.createdBy = null;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = null;
    }

    public AppDbAddress(CFLibDbKeyHash256 pid, CFLibDbKeyHash256 refUID, String addressName, String addressContact, String addressApartment,
        String addressStreet, String addressStreet2, String addressCity, String addressProvince, String addressCountry, String addressPostalCode) {
        this.pid = pid;
        this.refUID = refUID;
        this.addressName = addressName;
        this.addressContact = addressContact;
        this.addressApartment = addressApartment;
        this.addressStreet = addressStreet;
        this.addressStreet2 = addressStreet2;
        this.addressCity = addressCity;
        this.addressProvince = addressProvince;
        this.addressCountry = addressCountry;
        this.addressPostalCode = addressPostalCode;
        this.createdAt = LocalDateTime.now();
        this.createdBy = null;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = null;
    }

    public AppDbAddress(CFLibDbKeyHash256 pid, CFLibDbKeyHash256 refUID, String addressName, String addressContact, String addressApartment,
        String addressStreet, String addressStreet2, String addressCity, String addressProvince, String addressCountry, String addressPostalCode,
        java.time.LocalDateTime createdAt, CFLibDbKeyHash256 createdBy, java.time.LocalDateTime updatedAt, CFLibDbKeyHash256 updatedBy) {

        this.pid = pid;
        this.refUID = refUID;
        this.addressName = addressName;
        this.addressContact = addressContact;
        this.addressApartment = addressApartment;
        this.addressStreet = addressStreet;
        this.addressStreet2 = addressStreet2;
        this.addressCity = addressCity;
        this.addressProvince = addressProvince;
        this.addressCountry = addressCountry;
        this.addressPostalCode = addressPostalCode;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public CFLibDbKeyHash256 getPid() {
        return pid;
    }

    public void setPid(CFLibDbKeyHash256 pid) {
        if (pid == null || pid.isNull()) {
            throw new CFLibNullArgumentException(AppDbAddress.class, "setPid", 1, "pid");
        }
        this.pid = pid;
    }

    public CFLibDbKeyHash256 getRefUID() {
        return refUID;
    }

    public void setRefUID(CFLibDbKeyHash256 refUID) {
        if (refUID == null || refUID.isNull()) {
            throw new CFLibNullArgumentException(AppDbAddress.class, "setRefUID", 1, "refUID");
        }
        this.refUID = refUID;
    }

    public SecDbUser getUser() {
        if (refUID == null || refUID.isNull()) {
            return null;
        }
        else {
            SecDbUser user = secDbUserService.find(refUID);
            if (user == null) {
                throw new CFLibUnresolvedRelationException(AppDbAddress.class, "getUser", Inz.s("cflib.RelationType.Parent"), Inz.x("cflib.RelationType.Parent"), "refUID", "refUID", "SecDbUser", "SecDbUser", new Object() {
                    public CFLibDbKeyHash256 uid = refUID;
                });
            }
            else {
                return user;
            }
        }
    }

    public void setUser(SecDbUser user) {
        if (user == null || user.getPid() == null || user.getPid().isNull()) {
            throw new CFLibNullArgumentException(AppDbAddress.class, "setUser", 1, "user.pid");
        }
        refUID = user.getPid();
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        if (addressName == null || addressName.isEmpty()) {
            throw new CFLibNullArgumentException(AppDbAddress.class, "setAddressName", 1, "addressName");
        }
        if (addressName.length() > ADDR_NAME) {
            throw new CFLibArgumentOverflowException(AppDbAddress.class, "setAddressName", 1, "addressName.length", addressName.length(), ADDR_NAME);
        }
        this.addressName = addressName;
    }

    public String getAddressContact() {
        return addressContact;
    }

    public void setAddressContact(String addressContact) {
        if (addressContact != null && addressContact.isEmpty()) {
            throw new CFLibNullArgumentException(AppDbAddress.class, "setAddressContact", 1, "addressContact");
        }
        if (addressContact != null && addressContact.length() > ADDR_CONTACT) {
            throw new CFLibArgumentOverflowException(AppDbAddress.class, "setAddressContact", 1, "addressContact.length", addressContact.length(), ADDR_CONTACT);
        }
        this.addressContact = addressContact;
    }

    public String getAddressApartment() {
        return addressApartment;
    }

    public void setAddressApartment(String addressApartment) {
        if (addressApartment != null && addressApartment.isEmpty()) {
            throw new CFLibNullArgumentException(AppDbAddress.class, "setAddressApartment", 1, "addressApartment");
        }
        if (addressApartment != null && addressApartment.length() > ADDR_APARTMENT) {
            throw new CFLibArgumentOverflowException(AppDbAddress.class, "setAddressApartment", 1, "addressApartment.length", addressApartment.length(), ADDR_APARTMENT);
        }
        this.addressApartment = addressApartment;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public void setAddressStreet(String addressStreet) {
        if (addressStreet != null && addressStreet.isEmpty()) {
            throw new CFLibNullArgumentException(AppDbAddress.class, "setAddressStreet", 1, "addressStreet");
        }
        if (addressStreet != null && addressStreet.length() > ADDR_STREET) {
            throw new CFLibArgumentOverflowException(AppDbAddress.class, "setAddressStreet", 1, "addressStreet.length", addressStreet.length(), ADDR_STREET);
        }
        this.addressStreet = addressStreet;
    }

    public String getAddressStreet2() {
        return addressStreet2;
    }

    public void setAddressStreet2(String addressStreet2) {
        if (addressStreet2 != null && addressStreet2.isEmpty()) {
            throw new CFLibNullArgumentException(AppDbAddress.class, "setAddressStreet2", 1, "addressStreet2");
        }
        if (addressStreet2 != null && addressStreet2.length() > ADDR_STREET2) {
            throw new CFLibArgumentOverflowException(AppDbAddress.class, "setAddressStreet2", 1, "addressStreet2.length", addressStreet2.length(), ADDR_STREET2);
        }
        this.addressStreet2 = addressStreet2;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        if (addressCity != null && addressCity.isEmpty()) {
            throw new CFLibNullArgumentException(AppDbAddress.class, "setAddressCity", 1, "addressCity");
        }
        if (addressCity != null && addressCity.length() > ADDR_CITY) {
            throw new CFLibArgumentOverflowException(AppDbAddress.class, "setAddressCity", 1, "addressCity.length", addressCity.length(), ADDR_CITY);
        }
        this.addressCity = addressCity;
    }

    public String getAddressProvince() {
        return addressProvince;
    }

    public void setAddressProvince(String addressProvince) {
        if (addressProvince != null && addressProvince.isEmpty()) {
            throw new CFLibNullArgumentException(AppDbAddress.class, "setAddressProvince", 1, "addressProvince");
        }
        if (addressProvince != null && addressProvince.length() > ADDR_PROVINCE) {
            throw new CFLibArgumentOverflowException(AppDbAddress.class, "setAddressProvince", 1, "addressProvince.length", addressProvince.length(), ADDR_PROVINCE);
        }
        this.addressProvince = addressProvince;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(String addressCountry) {
        if (addressCountry != null && addressCountry.isEmpty()) {
            throw new CFLibNullArgumentException(AppDbAddress.class, "setAddressCountry", 1, "addressCountry");
        }
        if (addressCountry != null && addressCountry.length() > ADDR_COUNTRY) {
            throw new CFLibArgumentOverflowException(AppDbAddress.class, "setAddressCountry", 1, "addressCountry.length", addressCountry.length(), ADDR_COUNTRY);
        }
        this.addressCountry = addressCountry;
    }

    public String getAddressPostalCode() {
        return addressPostalCode;
    }

    public void setAddressPostalCode(String addressPostalCode) {
        if (addressPostalCode != null && addressPostalCode.isEmpty()) {
            throw new CFLibNullArgumentException(AppDbAddress.class, "setAddressPostalCode", 1, "addressPostalCode");
        }
        if (addressPostalCode != null && addressPostalCode.length() > ADDR_POSTAL_CODE) {
            throw new CFLibArgumentOverflowException(AppDbAddress.class, "setAddressPostalCode", 1, "addressPostalCode.length", addressPostalCode.length(), ADDR_POSTAL_CODE);
        }
        this.addressPostalCode = addressPostalCode;
    }
    
    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        if (createdAt == null) {
            throw new CFLibNullArgumentException(AppDbAddress.class, "setCreatedAt", 1, "createdAt");
        }
        this.createdAt = createdAt;
    }

    public CFLibDbKeyHash256 getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CFLibDbKeyHash256 createdBy) {
        if (createdBy == null) {
            throw new CFLibNullArgumentException(AppDbAddress.class, "setCreatedBy", 1, "createdBy");
        }
        this.createdBy = createdBy;
    }

    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        if (updatedAt == null) {
            throw new CFLibNullArgumentException(AppDbAddress.class, "setUpdatedAt", 1, "updatedAt");
        }
        this.updatedAt = updatedAt;
    }

    public CFLibDbKeyHash256 getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(CFLibDbKeyHash256 updatedBy) {
        if (updatedBy == null || updatedBy.isNull()) {
            throw new CFLibNullArgumentException(AppDbAddress.class, "setUpdatedBy", 1, "updatedBy");
        }
        this.updatedBy = updatedBy;
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        if (o == null || getClass() != o.getClass()) return 1;
        AppDbAddress that = (AppDbAddress) o;
        int cmp = this.pid.compareTo(that.pid);
        if (cmp != 0) return cmp;
        cmp = this.refUID == null ? (that.refUID == null ? 0 : -1) : (that.refUID == null ? 1 : this.refUID.compareTo(that.refUID));
        if (cmp != 0) return cmp;
        cmp = this.addressName.compareTo(that.addressName);
        if (cmp != 0) return cmp;
        cmp = this.addressContact == null ? (that.addressContact == null ? 0 : -1) : (that.addressContact == null ? 1 : this.addressContact.compareTo(that.addressContact));
        if (cmp != 0) return cmp;
        cmp = this.addressApartment == null ? (that.addressApartment == null ? 0 : -1) : (that.addressApartment == null ? 1 : this.addressApartment.compareTo(that.addressApartment));
        if (cmp != 0) return cmp;
        cmp = this.addressStreet == null ? (that.addressStreet == null ? 0 : -1) : (that.addressStreet == null ? 1 : this.addressStreet.compareTo(that.addressStreet));
        if (cmp != 0) return cmp;
        cmp = this.addressStreet2 == null ? (that.addressStreet2 == null ? 0 : -1) : (that.addressStreet2 == null ? 1 : this.addressStreet2.compareTo(that.addressStreet2));
        if (cmp != 0) return cmp;
        cmp = this.addressCity == null ? (that.addressCity == null ? 0 : -1) : (that.addressCity == null ? 1 : this.addressCity.compareTo(that.addressCity));
        if (cmp != 0) return cmp;
        cmp = this.addressProvince == null ? (that.addressProvince == null ? 0 : -1) : (that.addressProvince == null ? 1 : this.addressProvince.compareTo(that.addressProvince));
        if (cmp != 0) return cmp;
        cmp = this.addressCountry == null ? (that.addressCountry == null ? 0 : -1) : (that.addressCountry == null ? 1 : this.addressCountry.compareTo(that.addressCountry));
        if (cmp != 0) return cmp;
        cmp = this.addressPostalCode == null ? (that.addressPostalCode == null ? 0 : -1) : (that.addressPostalCode == null ? 1 : this.addressPostalCode.compareTo(that.addressPostalCode));
        return cmp;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppDbAddress that = (AppDbAddress) o;
        return 0 == this.pid.compareTo(that.pid) &&
               (this.refUID == null ? that.refUID == null : this.refUID.equals(that.refUID)) &&
               0 == this.addressName.compareTo(that.addressName) &&
               (this.addressContact == null ? that.addressContact == null : this.addressContact.equals(that.addressContact)) &&
               (this.addressApartment == null ? that.addressApartment == null : this.addressApartment.equals(that.addressApartment)) &&
               (this.addressStreet == null ? that.addressStreet == null : this.addressStreet.equals(that.addressStreet)) &&
               (this.addressStreet2 == null ? that.addressStreet2 == null : this.addressStreet2.equals(that.addressStreet2)) &&
               (this.addressCity == null ? that.addressCity == null : this.addressCity.equals(that.addressCity)) &&
               (this.addressProvince == null ? that.addressProvince == null : this.addressProvince.equals(that.addressProvince)) &&
               (this.addressCountry == null ? that.addressCountry == null : this.addressCountry.equals(that.addressCountry)) &&
               (this.addressPostalCode == null ? that.addressPostalCode == null : this.addressPostalCode.equals(that.addressPostalCode));
    }

    @Override
    public final int hashCode() {
        int hc = pid == null ? 0 : pid.hashCode();
        hc = 31 * hc + (refUID == null ? 0 : refUID.hashCode());
        hc = 31 * hc + (addressName == null ? 0 : addressName.hashCode());
        hc = 31 * hc + (addressContact == null ? 0 : addressContact.hashCode());
        hc = 31 * hc + (addressApartment == null ? 0 : addressApartment.hashCode());
        hc = 31 * hc + (addressStreet == null ? 0 : addressStreet.hashCode());
        hc = 31 * hc + (addressStreet2 == null ? 0 : addressStreet2.hashCode());
        hc = 31 * hc + (addressCity == null ? 0 : addressCity.hashCode());
        hc = 31 * hc + (addressProvince == null ? 0 : addressProvince.hashCode());
        hc = 31 * hc + (addressCountry == null ? 0 : addressCountry.hashCode());
        hc = 31 * hc + (addressPostalCode == null ? 0 : addressPostalCode.hashCode());
        return hc;
    }
}
