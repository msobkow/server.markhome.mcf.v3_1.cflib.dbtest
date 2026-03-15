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
package server.markhome.mcf.v3_1.cflib.dbtest.spring;

import java.time.LocalDateTime;
import java.util.List;

import server.markhome.mcf.v3_1.cflib.dbtest.secdb.SecDbManager;
import server.markhome.mcf.v3_1.cflib.dbtest.secdb.SecDbManagerService;
import server.markhome.mcf.v3_1.cflib.dbtest.secdb.SecDbSession;
import server.markhome.mcf.v3_1.cflib.dbtest.secdb.SecDbSessionService;
import server.markhome.mcf.v3_1.cflib.dbtest.secdb.SecDbUser;
import server.markhome.mcf.v3_1.cflib.dbtest.secdb.SecDbUserService;
import server.markhome.mcf.v3_1.cflib.dbutil.CFLibDbKeyHash256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@Service("TestSecDb")
public class TestSecDb {
    
    @Autowired
    @Qualifier("secEntityManagerFactory")
    private LocalContainerEntityManagerFactoryBean secEntityManagerFactory;

    @Autowired
    private SecDbUserService secDbUserService;

    @Autowired
    private SecDbManagerService secDbManagerService;

    @Autowired
    private SecDbSessionService secDbSessionService;
    
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    // @PersistenceContext(unitName = "SecDbPU")
    public String performTests(EntityManager em) {
        StringBuffer responseMessage = new StringBuffer();
        try {
            LocalDateTime now = LocalDateTime.now();
            CFLibDbKeyHash256 adminpid = new CFLibDbKeyHash256("0123456789abcdef");
            CFLibDbKeyHash256 mgrpid = new CFLibDbKeyHash256("fedcba9876543210");
            SecDbManager manager = (SecDbManager)secDbManagerService.find(mgrpid);
            if (manager == null) {
                manager = new SecDbManager(mgrpid, "system", "admin", "1", "System Administration", "1",
                          null, null,
                          now, mgrpid,
                          now, mgrpid);
                manager = secDbManagerService.create(manager);
                String msg = "INFO: Sample SecDbManager 'system' fedcba9876543210 created, update stamp is " + manager.getUpdatedAt().toString() + "\n";
                responseMessage.append(msg);
            }
            else {
                manager.setUpdatedBy(adminpid);
                manager = secDbManagerService.update(manager);
                String msg = "INFO: Sample SecDbManager 'system' fedcba9876543210 updated, update stamp is " + manager.getUpdatedAt().toString() + "\n";
                responseMessage.append(msg);
            }
            SecDbUser user = secDbUserService.find(adminpid);
            if (user == null) {
                user = new SecDbUser(adminpid, "admin", "root", "1", now, mgrpid, now, mgrpid);
                user = secDbUserService.create(user);
                String msg = "INFO: Sample SecDbUser 'admin' 0123456789abcdef created.\n";
                responseMessage.append(msg);
            }
            else {
                user.setUpdatedBy(adminpid);
                user = secDbUserService.update(user);
                String msg = "INFO: Sample SecDbUser 'admin' 0123456789abcdef updated, update stamp is " + user.getUpdatedAt().toString() + "\n";
                responseMessage.append(msg);
            }
            List<SecDbSession> managerSessions = secDbSessionService.findByUser(manager);
            if (managerSessions == null || managerSessions.isEmpty()) {
                SecDbSession sess = new SecDbSession(mgrpid, manager, "System initialization", now, null, null);
                sess = secDbSessionService.create(sess);
                String msg = "INFO Priming SecDbSession " + sess.getPid().toString() + " for system initialization created\n";
                responseMessage.append(msg);
            }
            else {
                if (managerSessions.size() == 1) {
                    SecDbSession sess = managerSessions.get(0);
                    if (sess.getTerminatedAt() == null) {
                        sess.setTerminatedAt(now);
                        sess.setSessTerminationInfo("First rerun auto-terminates the initialization session");
                        sess = secDbSessionService.update(sess);
                        String msg = "INFO Terminated last run SecDbSession " + sess.getPid().toString() + " from system initialization\n";
                        responseMessage.append(msg);
                    }
                    else {
                        String msg = "INFO SecDbSession " + sess.getPid().toString() + " from system initialization was terminated at " + sess.getTerminatedAt() + "\n";
                        responseMessage.append(msg);
                    }
                }
                else {
                    String msg = "INFO Multiple SecDbSession instances indicate initialization happened some time ago.\n";
                    responseMessage.append(msg);
                }
            }
        }
        catch (Exception e) {
            String msg = "ERROR: TestSecDb.performTests() Caught and rethrew " + e.getClass().getCanonicalName() + " while modifying or creating the 'system' manager and the 'admin' user - " + e.getMessage() + "\n";
            responseMessage.append(msg);
            System.err.println(msg);
            e.printStackTrace(System.err);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return responseMessage.toString();
    }
}
