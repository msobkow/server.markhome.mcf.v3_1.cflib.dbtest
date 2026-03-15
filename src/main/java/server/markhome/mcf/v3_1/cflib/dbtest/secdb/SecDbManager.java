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
 *	Version 3 or later.
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

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;

import java.util.Set;
import java.util.HashSet;

import server.markhome.mcf.v3_1.cflib.CFLibArgumentOverflowException;
import server.markhome.mcf.v3_1.cflib.CFLibNullArgumentException;
import server.markhome.mcf.v3_1.cflib.dbutil.CFLibDbKeyHash256;

@Entity
@DiscriminatorValue("1")
@Table(
    name = "sec_mgr", schema = "secdb",
    indexes = {
        @Index(name = "sec_mgr_deptcode_ax", columnList = "deptcode", unique = true)
    }
)
@Transactional(Transactional.TxType.SUPPORTS)
@PersistenceContext(unitName = "SecDbPU")
public class SecDbManager extends SecDbUser {
    
    public static final int TITLE_SIZE = 64;
    public static final int DEPARTMENT_CODE_SIZE = 32;

    @Column(name = "title", length = TITLE_SIZE, nullable = false)
    private String title = "";

    @Column(name = "deptcode", length = DEPARTMENT_CODE_SIZE, nullable = false, unique = true)
    private String departmentCode = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subdeptof")
    @AttributeOverrides({
        @AttributeOverride(name = "bytes", column = @Column(name = "subdeptof", nullable = false, unique = false, length = CFLibDbKeyHash256.HASH_LENGTH))
    })
    private SecDbManager subDepartmentOf;

    @OneToMany(mappedBy = "subDepartmentOf", fetch = FetchType.LAZY)
    private Set<SecDbManager> departments = new HashSet<>();

    public SecDbManager() {
        super();
    }

    public SecDbManager(CFLibDbKeyHash256 pid) {
        super(pid);
        this.title = "";
        this.departmentCode = "";
    }

    public SecDbManager(CFLibDbKeyHash256 pid, SecDbUser user) {
        super(pid, user.getUsername(), user.getEmail());
        this.title = "";
        this.departmentCode = "";
    }

    public SecDbManager(CFLibDbKeyHash256 pid, String username, String email) {
        super(pid, username, email);
        this.title = "";
        this.departmentCode = "";
    }

    public SecDbManager(CFLibDbKeyHash256 pid, String username, String email, String title, String departmentCode) {
        super(pid, username, email);
        this.title = title;
        this.departmentCode = departmentCode;
    }

    public SecDbManager(CFLibDbKeyHash256 pid, String username, String email, String title, String departmentCode, SecDbManager subDepartmentOf) {
        super(pid, username, email);
        this.title = title;
        this.departmentCode = departmentCode;
        this.subDepartmentOf = subDepartmentOf;
    }

    public SecDbManager(CFLibDbKeyHash256 pid, String username, String email, String title, String departmentCode, SecDbManager subDepartmentOf, Set<SecDbManager> departments) {
        super(pid, username, email);
        this.title = title;
        this.departmentCode = departmentCode;
        this.subDepartmentOf = subDepartmentOf;
        this.departments = departments != null ? departments : new HashSet<>();
    }

    public SecDbManager(CFLibDbKeyHash256 pid, String username, String email, String memberDeptCode, String title, String departmentCode,
                          SecDbManager subDepartmentOf, Set<SecDbManager> departments,
                          java.time.LocalDateTime createdAt, CFLibDbKeyHash256 createdBy,
                          java.time.LocalDateTime updatedAt, CFLibDbKeyHash256 updatedBy) {
        super(pid, username, email, memberDeptCode, createdAt, createdBy, updatedAt, updatedBy);
        this.title = title;
        this.departmentCode = departmentCode;
        this.subDepartmentOf = subDepartmentOf;
        this.departments = departments != null ? departments : new HashSet<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new CFLibNullArgumentException(SecDbManager.class, "setTitle", 1, "title");
        }
        if (title.length() > TITLE_SIZE) {
            throw new CFLibArgumentOverflowException(SecDbManager.class, "setTitle", 1, "title.length", title.length(), TITLE_SIZE);
        }
        this.title = title;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        if (departmentCode == null || departmentCode.isEmpty()) {
            throw new CFLibNullArgumentException(SecDbManager.class, "setDepartmentCode", 1, "departmentCode");
        }
        if (departmentCode.length() > DEPARTMENT_CODE_SIZE) {
            throw new CFLibArgumentOverflowException(SecDbManager.class, "setDepartmentCode", 1, "departmentCode.length", departmentCode.length(), DEPARTMENT_CODE_SIZE);
        }
        this.departmentCode = departmentCode;
    }

    public SecDbManager getSubDepartmentOf() {
        return subDepartmentOf;
    }

    public void setSubDepartmentOf(SecDbManager subDepartmentOf) {
        this.subDepartmentOf = subDepartmentOf;
    }

    public Set<SecDbManager> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<SecDbManager> departments) {
        this.departments = departments;
    }

    public void addDepartment(SecDbManager department) {
        if (department != null) {
            departments.add(department);
            department.setSubDepartmentOf(this);
        }
    }

    public void removeDepartment(SecDbManager department) {
        if (department != null) {
            departments.remove(department);
            department.setSubDepartmentOf(null);
        }
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        if (!(o instanceof SecDbManager)) return -1;
        SecDbManager other = (SecDbManager) o;
        int cmp = super.compareTo(other);
        if (cmp != 0) return cmp;
        cmp = (this.title == null && other.title == null) ? 0 : ((this.title != null && this.title.equals(other.title)) ? 0 : (this.title == null ? -1 : (other.title == null ? 1 : this.title.compareTo(other.title))));
        if (cmp != 0) return cmp;
        cmp = (this.departmentCode == null && other.departmentCode == null) ? 0 : ((this.departmentCode != null && this.departmentCode.equals(other.departmentCode)) ? 0 : (this.departmentCode == null ? -1 : (other.departmentCode == null ? 1 : this.departmentCode.compareTo(other.departmentCode))));
        if (cmp != 0) return cmp;
        cmp = (this.subDepartmentOf == null && other.subDepartmentOf == null) ? 0 : ((this.subDepartmentOf != null && other.subDepartmentOf != null && this.subDepartmentOf.getPid().equals((other.subDepartmentOf.getPid()))) ? 0 : (this.subDepartmentOf == null ? -1 : (other.subDepartmentOf == null ? 1 : this.subDepartmentOf.getPid().compareTo(other.subDepartmentOf.getPid()))));
        return cmp;
    }

}
