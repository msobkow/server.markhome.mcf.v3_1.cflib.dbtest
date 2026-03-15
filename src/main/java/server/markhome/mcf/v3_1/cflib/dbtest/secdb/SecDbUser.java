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
package server.markhome.mcf.v3_1.cflib.dbtest.secdb;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import org.hibernate.annotations.UpdateTimestamp;

import org.hibernate.annotations.CreationTimestamp;

import server.markhome.mcf.v3_1.cflib.CFLibArgumentOverflowException;
import server.markhome.mcf.v3_1.cflib.CFLibNullArgumentException;
import server.markhome.mcf.v3_1.cflib.dbutil.CFLibDbKeyHash256;

@Entity
@Table(
    name = "sec_user", schema = "secdb",
    indexes = {
        @Index(name = "sec_user_pidx", columnList = "pid", unique = true),
        @Index(name = "sec_user_axname", columnList = "username", unique = true),
        @Index(name = "sec_user_dxemail", columnList = "email", unique = false),
        @Index(name = "sec_user_dxmbrdptcd", columnList = "member_deptcode", unique = false),
    }
)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.INTEGER)
@DiscriminatorValue("0")
@Transactional(Transactional.TxType.SUPPORTS)
@PersistenceContext(unitName = "SecDbPU")
public class SecDbUser implements Comparable<Object> {
    public static final int USERNAME_SIZE = 64;
    public static final int EMAIL_SIZE = 1023;

    @Id
    @AttributeOverrides({
        @AttributeOverride(name = "bytes", column = @Column(name = "pid", nullable = false, unique = true, length = CFLibDbKeyHash256.HASH_LENGTH))
    })
    private CFLibDbKeyHash256 pid;

    @Column(name = "username", nullable = false, unique = true, length = USERNAME_SIZE)
    private String username;

    @Column(name = "email", nullable = false, unique = false, length = EMAIL_SIZE)
    private String email;

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

    @Column(name = "member_deptcode", length = SecDbManager.DEPARTMENT_CODE_SIZE, nullable = true)
    private String memberDeptCode;

    public SecDbUser() {}

    public SecDbUser(CFLibDbKeyHash256 pid) {
        this.pid = pid;
        this.username = "";
        this.email = "";
    }

    public SecDbUser(CFLibDbKeyHash256 pid, SecDbUser user) {
        this.pid = pid;
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.memberDeptCode = user.getMemberDeptCode();
    }
    
    public SecDbUser(CFLibDbKeyHash256 pid, String username, String email) {
        this.pid = pid;
        this.username = username;
        this.email = email;
    }

    public SecDbUser(CFLibDbKeyHash256 pid, String username, String email, String memberDeptCode) {
        this.pid = pid;
        this.username = username;
        this.email = email;
        this.memberDeptCode = memberDeptCode;
    }

    public SecDbUser(CFLibDbKeyHash256 pid, String username, String email, String memberDeptCode,
                     java.time.LocalDateTime createdAt, CFLibDbKeyHash256 createdBy,
                     java.time.LocalDateTime updatedAt, CFLibDbKeyHash256 updatedBy) {
        this.pid = pid;
        this.username = username;
        this.email = email;
        this.memberDeptCode = memberDeptCode;
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
            throw new CFLibNullArgumentException(SecDbUser.class, "setPid", 1, "pid");
        }
        this.pid = pid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new CFLibNullArgumentException(SecDbUser.class, "setUsername", 1, "username");
        }
        if (username.length() > USERNAME_SIZE) {
            throw new CFLibArgumentOverflowException(SecDbUser.class, "setUsername", 1, "username.length", username.length(), USERNAME_SIZE);
        }
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new CFLibNullArgumentException(SecDbUser.class, "setEmail", 1, "email");
        }
        if (email.length() > EMAIL_SIZE) {
            throw new CFLibArgumentOverflowException(SecDbUser.class, "setEmail", 1, "email.length", email.length(), EMAIL_SIZE);
        }
        this.email = email;
    }
    
    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        if (createdAt == null) {
            throw new CFLibNullArgumentException(SecDbUser.class, "setCreatedAt", 1, "createdAt");
        }
        this.createdAt = createdAt;
    }

    public CFLibDbKeyHash256 getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CFLibDbKeyHash256 createdBy) {
        if (createdBy == null) {
            throw new CFLibNullArgumentException(SecDbUser.class, "setCreatedBy", 1, "createdBy");
        }
        this.createdBy = createdBy;
    }

    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        if (updatedAt == null) {
            throw new CFLibNullArgumentException(SecDbUser.class, "setUpdatedAt", 1, "updatedAt");
        }
        this.updatedAt = updatedAt;
    }

    public CFLibDbKeyHash256 getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(CFLibDbKeyHash256 updatedBy) {
        if (updatedBy == null || updatedBy.isNull()) {
            throw new CFLibNullArgumentException(SecDbUser.class, "setUpdatedBy", 1, "updatedBy");
        }
        this.updatedBy = updatedBy;
    }

    public String getMemberDeptCode() {
        return memberDeptCode;
    }

    public void setMemberDeptCode(String memberDeptCode) {
        if (memberDeptCode == null || memberDeptCode.isEmpty()) {
            throw new CFLibNullArgumentException(SecDbUser.class, "setMemberDeptCode", 1, "memberDeptCode");
        }
        if (memberDeptCode.length() > SecDbManager.DEPARTMENT_CODE_SIZE) {
            throw new CFLibArgumentOverflowException(SecDbUser.class, "setMemberDeptCode", 1, "memberDeptCode.length", memberDeptCode.length(), SecDbManager.DEPARTMENT_CODE_SIZE);
        }
        this.memberDeptCode = memberDeptCode;
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        if (o == null || getClass() != o.getClass()) return 1;
        SecDbUser that = (SecDbUser) o;
        int cmp = this.pid.compareTo(that.pid);
        if (cmp != 0) return cmp;
        cmp = this.username.compareTo(that.username);
        if (cmp != 0) return cmp;
        cmp = this.email.compareTo(that.email);
        if (cmp != 0) return cmp;

        // Compare memberDeptCode
        cmp = ((this.memberDeptCode == null && that.memberDeptCode == null) ? 0 :
               (this.memberDeptCode != null && that.memberDeptCode != null && this.memberDeptCode.equals(that.memberDeptCode)) ? 0 :
               (this.memberDeptCode == null ? -1 : (that.memberDeptCode == null ? 1 : this.memberDeptCode.compareTo(that.memberDeptCode))));
        return cmp;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SecDbUser that = (SecDbUser) o;
        return 0 == this.pid.compareTo(that.pid) &&
                0 == this.username.compareTo(that.username) &&
                0 == this.email.compareTo(that.email) &&
                ((this.memberDeptCode == null && that.memberDeptCode == null) ||
                 (this.memberDeptCode != null && that.memberDeptCode != null && this.memberDeptCode.equals(that.memberDeptCode)));
    }

    @Override
    public final int hashCode() {
        int hc = pid == null ? 0 : pid.hashCode();
        hc = 31 * hc + (username == null ? 0 : username.hashCode());
        hc = 31 * hc + (email == null ? 0 : email.hashCode());
        hc = 31 * hc + (memberDeptCode == null ? 0 : memberDeptCode.hashCode());
        return hc;
    }
}
