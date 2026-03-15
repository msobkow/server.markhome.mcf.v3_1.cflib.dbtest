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
package server.markhome.mcf.v3_1.cflib.dbtest.appdb;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import server.markhome.mcf.v3_1.cflib.CFLibDbException;
import server.markhome.mcf.v3_1.cflib.CFLibNullArgumentException;
import server.markhome.mcf.v3_1.cflib.CFLibUnresolvedRelationException;
import server.markhome.mcf.v3_1.cflib.dbtest.secdb.SecDbSessionService;
import server.markhome.mcf.v3_1.cflib.dbtest.secdb.SecDbUser;
import server.markhome.mcf.v3_1.cflib.dbtest.secdb.SecDbUserService;
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

@Service("AppDbAddressService")
public class AppDbAddressService {

    @Autowired
    @Qualifier("appEntityManagerFactory")
    private LocalContainerEntityManagerFactoryBean appEntityManagerFactory;
    
    @Autowired
    private AppDbAddressRepository appDbAddressRepository;

    @Autowired
    private SecDbUserService secDbUserService;

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "appTransactionManager")
    public AppDbAddress find(CFLibDbKeyHash256 pid) {
        return appDbAddressRepository.findById(pid).orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "appTransactionManager")
    public List<AppDbAddress> findByRefUID(CFLibDbKeyHash256 refUID) {
        if (refUID == null || refUID.isNull()) {
            return new ArrayList<>();
        }
        return appDbAddressRepository.findByRefUID(refUID);
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "appTransactionManager")
    public List<AppDbAddress> findByUser(SecDbUser user) {
        if (user == null || user.getPid() == null || user.getPid().isNull()) {
            return new ArrayList<>();
        }
        return appDbAddressRepository.findByRefUID(user.getPid());
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "appTransactionManager")
    public AppDbAddress findByRefUIDName(CFLibDbKeyHash256 refUID, String addressName) {
        if (refUID == null || refUID.isNull() || addressName == null || addressName.isEmpty()) {
            return null;
        }
        AppDbAddress probe = new AppDbAddress();
        probe.setRefUID(refUID);
        probe.setAddressName(addressName);

        ExampleMatcher matcher = ExampleMatcher.matching()
            .withIgnoreNullValues()
            .withMatcher("refuid, addrname", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<AppDbAddress> example = Example.of(probe, matcher);

        return appDbAddressRepository.findOne(example).orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "appTransactionManager")
    public AppDbAddress findByUserName(SecDbUser user, String addressName) {
        if (user == null || user.getPid() == null || user.getPid().isNull() || addressName == null || addressName.isEmpty()) {
            return null;
        }
        AppDbAddress probe = new AppDbAddress();
        probe.setRefUID(user.getPid());
        probe.setAddressName(addressName);

        ExampleMatcher matcher = ExampleMatcher.matching()
            .withIgnoreNullValues()
            .withMatcher("refuid, addrname", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<AppDbAddress> example = Example.of(probe, matcher);

        return appDbAddressRepository.findOne(example).orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = NoResultException.class, transactionManager = "appTransactionManager")
    public AppDbAddress create(AppDbAddress data) {
        if (data == null) {
            return null;
        }
        if (data.getRefUID() == null || data.getRefUID().isNull()) {
            throw new CFLibNullArgumentException(AppDbAddressService.class, "create", 1, "data.refUID");
        }
        SecDbUser user = secDbUserService.find(data.getRefUID());
        if (user == null) {
            throw new CFLibUnresolvedRelationException(AppDbAddressService.class, "create", Inz.s("cflib.RelationType.Parent"), Inz.x("cflib.RelationType.Parent"), "refUID", "refUID", "SecDbUser", "SecDbUser", new Object() {
                public CFLibDbKeyHash256 uid = data.getRefUID();
            });
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
            if (data.getPid() != null && appDbAddressRepository.existsById(data.getPid())) {
                return appDbAddressRepository.findById(data.getPid()).orElse(null);
            }

            return appDbAddressRepository.save(data);
        } catch (Exception e) {
            // Remove auto-generated pid if there was an error
            if (generatedPid) {
                data.setPid(originalPid);
            }
            System.err.println(String.format(Inz.x("cflib.dbtest.AppDbAddressService.rethrow"), e.getClass().getName(), "create", e.getLocalizedMessage()));
            e.printStackTrace(System.err);
            throw new CFLibDbException(SecDbSessionService.class, "create", String.format(Inz.s("cflib.dbtest.AppDbAddressService.rethrow"), e.getClass().getName(), e.getMessage()), "create", String.format(Inz.x("cflib.dbtest.AppDbAddressService.rethrow"), e.getClass().getName(), "create", e.getLocalizedMessage()), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = NoResultException.class, transactionManager = "appTransactionManager")
    public AppDbAddress update(AppDbAddress data) {
        if (data == null) {
            return null;
        }
        if (data.getPid() == null || data.getPid().isNull()) {
            throw new CFLibNullArgumentException(AppDbAddressService.class, "update", 1, "data.pid");
        }
        if (data.getRefUID() == null || data.getRefUID().isNull()) {
            throw new CFLibNullArgumentException(AppDbAddressService.class, "update", 1, "data.refUID");
        }
        SecDbUser user = secDbUserService.find(data.getRefUID());
        if (user == null) {
            throw new CFLibUnresolvedRelationException(AppDbAddressService.class, "update", Inz.s("cflib.RelationType.Parent"), Inz.x("cflib.RelationType.Parent"), "refUID", "refUID", "SecDbUser", "SecDbUser", new Object() {
                public CFLibDbKeyHash256 uid = data.getRefUID();
            });
        }

        // Check if the entity exists
        AppDbAddress existing = appDbAddressRepository.findById(data.getPid())
            .orElseThrow(() -> new NoResultException("AppDbAddress with pid " + data.getPid() + " does not exist"));

        // Update fields (except pid, createdAt)
        existing.setRefUID(data.getRefUID());
        existing.setAddressName(data.getAddressName());
        existing.setAddressApartment(data.getAddressApartment());
        existing.setAddressCity(data.getAddressCity());
        existing.setAddressContact(data.getAddressContact());
        existing.setAddressCountry(data.getAddressCountry());
        existing.setAddressPostalCode(data.getAddressPostalCode());
        existing.setAddressProvince(data.getAddressProvince());
        existing.setAddressStreet(data.getAddressStreet());
        existing.setAddressStreet2(data.getAddressStreet2());

        // ... update other fields as needed ...
        existing.setUpdatedAt(LocalDateTime.now());

        return appDbAddressRepository.save(existing);
    }
}
