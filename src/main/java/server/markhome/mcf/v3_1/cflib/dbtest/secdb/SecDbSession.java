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

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import server.markhome.mcf.v3_1.cflib.CFLibNullArgumentException;
import server.markhome.mcf.v3_1.cflib.dbutil.CFLibDbKeyHash256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;

@Entity
@Table(name = "sec_sess", schema = "secdb")
@Transactional(Transactional.TxType.SUPPORTS)
@PersistenceContext(unitName = "SecDbPU")
public class SecDbSession {
    public final static int SESS_CREATE_INFO_LEN = 1024;
    public final static int SESS_TERMINATION_INFO_LEN = 1024;

    @Id
    @AttributeOverrides({
        @AttributeOverride(name = "bytes", column = @Column(name = "pid", nullable = false, unique = true, length = CFLibDbKeyHash256.HASH_LENGTH))
    })
    private CFLibDbKeyHash256 pid;

    @ManyToOne(fetch = FetchType.LAZY)
    @AttributeOverrides({
        @AttributeOverride(name = "bytes", column = @Column(name = "secuser_pid", nullable = false, unique = false, length = CFLibDbKeyHash256.HASH_LENGTH))
    })
    private SecDbUser secUser;

    @Column(name = "sess_cr_info", nullable = false, updatable = false, length = SESS_CREATE_INFO_LEN)
    private String sessCreateInfo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "sess_term_info", nullable = true, updatable = true, length = SESS_TERMINATION_INFO_LEN)
    private String sessTerminationInfo;

    @Column(name = "terminated_at", nullable = true, updatable = true)
    private LocalDateTime terminatedAt;

    @Autowired
    @Qualifier("secEntityManagerFactory")
    private static EntityManagerFactory secEntityManagerFactory;
  
    public SecDbSession() {}

    public SecDbSession(CFLibDbKeyHash256 pid, SecDbUser secUser) {
        this.pid = pid;
        this.secUser = secUser;
    }

    public SecDbSession(CFLibDbKeyHash256 pid, SecDbUser secUser, String sessCreateInfo) {
        this.pid = pid;
        this.secUser = secUser;
        this.sessCreateInfo = sessCreateInfo;
        this.createdAt = LocalDateTime.now();
        this.sessTerminationInfo = null;
        this.terminatedAt = null;
    }

    public SecDbSession(CFLibDbKeyHash256 pid, SecDbUser secUser, String sessCreateInfo, LocalDateTime createdAt) {
        this.pid = pid;
        this.secUser = secUser;
        this.sessCreateInfo = sessCreateInfo;
        this.createdAt = createdAt;
        this.sessTerminationInfo = null;
        this.terminatedAt = null;
    }

    public SecDbSession(CFLibDbKeyHash256 pid, SecDbUser secUser, String sessCreateInfo, LocalDateTime createdAt, String sessTerminationInfo, LocalDateTime terminatedAt) {
        this.pid = pid;
        this.secUser = secUser;
        this.sessCreateInfo = sessCreateInfo;
        this.createdAt = createdAt;
        this.sessTerminationInfo = sessTerminationInfo;
        this.terminatedAt = terminatedAt;
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

    public SecDbUser getSecUser() {
        return secUser;
    }

    public void setSecUser(SecDbUser secUser) {
        if (secUser == null || secUser.getPid() == null || secUser.getPid().isNull()) {
            throw new CFLibNullArgumentException(SecDbUser.class, "setSecUser", 1, "secUser.pid");
        }
        this.secUser = secUser;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) {
            throw new CFLibNullArgumentException(SecDbUser.class, "setCreatedAt", 1, "createdAt");
        }
        this.createdAt = createdAt;
    }

    public String getSessTerminationInfo() {
        return sessTerminationInfo;
    }

    public void setSessTerminationInfo(String sessTerminationInfo) {
        if (sessTerminationInfo != null && sessTerminationInfo.isEmpty()) {
            throw new CFLibNullArgumentException(SecDbSession.class, "setSessTerminationInfo", 1, "sessTerminationInfo");
        }
        this.sessTerminationInfo = sessTerminationInfo;
    }

    public LocalDateTime getTerminatedAt() {
        return terminatedAt;
    }

    public void setTerminatedAt(LocalDateTime terminatedAt) {
        this.terminatedAt = terminatedAt;
    }
}
