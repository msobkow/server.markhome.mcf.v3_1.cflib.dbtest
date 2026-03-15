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
import java.util.ArrayList;
import java.util.List;

import server.markhome.mcf.v3_1.cflib.CFLibDbException;
import server.markhome.mcf.v3_1.cflib.CFLibNullArgumentException;
import server.markhome.mcf.v3_1.cflib.dbutil.CFLibDbKeyHash256;
import server.markhome.mcf.v3_1.cflib.inz.Inz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import jakarta.persistence.NoResultException;

@Service("SecDbManagerService")
public class SecDbManagerService {

    @Autowired
    @Qualifier("secEntityManagerFactory")
    private LocalContainerEntityManagerFactoryBean secEntityManagerFactoryBean;
    
    @Autowired
    private SecDbManagerRepository secDbManagerRepository;

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbUser find(CFLibDbKeyHash256 pid) {
        return secDbManagerRepository.findById(pid).orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbUser findByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        SecDbManager probe = new SecDbManager();
        probe.setUsername(name);

        ExampleMatcher matcher = ExampleMatcher.matching()
            .withIgnoreNullValues()
            .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<SecDbManager> example = Example.of(probe, matcher);

        return secDbManagerRepository.findOne(example).orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public List<SecDbUser> findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return new ArrayList<>();
        }
        List<SecDbManager> l = secDbManagerRepository.findByEmail(email);
        List<SecDbUser> t = new ArrayList<>(l);
        return t;
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public List<SecDbUser> findByMemberDeptCode(String memberDeptCode) {
        if (memberDeptCode == null || memberDeptCode.isEmpty()) {
            return new ArrayList<>();
        }
        List<SecDbManager> l = secDbManagerRepository.findByMemberDeptCode(memberDeptCode);
        List<SecDbUser> t = new ArrayList<>(l);
        return t;
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public List<SecDbManager> findByDeptCode(String deptCode) {
        if (deptCode == null || deptCode.isEmpty()) {
            return null;
        }
        SecDbManager probe = new SecDbManager();
        probe.setDepartmentCode(deptCode);

        ExampleMatcher matcher = ExampleMatcher.matching()
            .withIgnoreNullValues()
            .withMatcher("deptcode", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<SecDbManager> example = Example.of(probe, matcher);

        return secDbManagerRepository.findAll(example);
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbManager create(SecDbManager data) {
        if (data == null) {
            return null;
        }
        CFLibDbKeyHash256 originalPid = data.getPid();
        boolean generatedPid = false;
        try {
            if (data.getPid() == null) {
                data.setPid(new CFLibDbKeyHash256(0));
                generatedPid = true;
            }
            LocalDateTime now = LocalDateTime.now();
            data.setCreatedAt(now);
            data.setUpdatedAt(now);

            // Check if already exists
            if (data.getPid() != null && secDbManagerRepository.existsById(data.getPid())) {
                return secDbManagerRepository.findById(data.getPid()).orElse(null);
            }

            return secDbManagerRepository.save(data);
        } catch (Exception e) {
            // Remove auto-generated pid if there was an error
            if (generatedPid) {
                data.setPid(originalPid);
            }
            System.err.println(String.format(Inz.x("cflib.dbtest.SecDbManagerService.rethrow"), e.getClass().getName(), "create", e.getLocalizedMessage()));
            e.printStackTrace(System.err);
            throw new CFLibDbException(SecDbManagerService.class, "create", String.format(Inz.s("cflib.dbtest.SecDbManagerService.rethrow"), e.getClass().getName(), e.getMessage()), "create", String.format(Inz.x("cflib.dbtest.SecDbManagerService.rethrow"), e.getClass().getName(), "create", e.getLocalizedMessage()), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbManager update(SecDbManager data) {
        if (data == null) {
            return null;
        }
        if (data.getPid() == null || data.getPid().isNull()) {
            throw new CFLibNullArgumentException(SecDbManagerService.class, "update", 1, "data.pid");
        }

        // Check if the entity exists
        SecDbManager existing = secDbManagerRepository.findById(data.getPid())
            .orElseThrow(() -> new NoResultException("SecDbManager with pid " + data.getPid() + " does not exist"));

        // Update fields (except pid, createdAt)
        existing.setUsername(data.getUsername());
        existing.setEmail(data.getEmail());
        existing.setMemberDeptCode(data.getMemberDeptCode());
        existing.setSubDepartmentOf(data.getSubDepartmentOf());
        existing.setTitle(data.getTitle());
        // ... update other fields as needed ...
        existing.setUpdatedAt(LocalDateTime.now());

        return secDbManagerRepository.save(existing);
    }
}
