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

import server.markhome.mcf.v3_1.cflib.dbtest.appdb.AppDbAddress;
import server.markhome.mcf.v3_1.cflib.dbtest.appdb.AppDbAddressService;
import server.markhome.mcf.v3_1.cflib.dbutil.CFLibDbKeyHash256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

@Service("TestAppDb")
public class TestAppDb {

    @Autowired
    @Qualifier("appEntityManagerFactory")
    private LocalContainerEntityManagerFactoryBean appEntityManagerFactoryBean;

    @Autowired
    private AppDbAddressService appDbAddressService;
    
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, dontRollbackOn = NoResultException.class)
    // @PersistenceContext(unitName = "AppDbPU")
    public String performTests(EntityManager em) {
        StringBuffer responseMessage = new StringBuffer();
        LocalDateTime now = LocalDateTime.now();
        // CFLibDbKeyHash256 adminpid = new CFLibDbKeyHash256("0123456789abcdef");
        CFLibDbKeyHash256 mgrpid = new CFLibDbKeyHash256("fedcba9876543210");
        List<AppDbAddress> addresses = appDbAddressService.findByRefUID(mgrpid);
        if (addresses == null || addresses.isEmpty()) {
            AppDbAddress appAddress = new AppDbAddress(new CFLibDbKeyHash256(0), mgrpid, "Home", "Mark Sobkow", "19", "207 Seventh Avenue North", null, "Yorkton", "SK", "Canada", "S3N 0X3", now, mgrpid, now, mgrpid);
            appAddress = appDbAddressService.create(appAddress);
            responseMessage.append("Sample AppDbAddress for Manager " + mgrpid.toString() + " created in AppDb.\n");
        } else {
            responseMessage.append("Sample AppDbAddress already exists for Manager " + mgrpid.toString() + ", or at least there isn't an empty list we can assume indicates a clean database\n");
        }
        return responseMessage.toString();
    }
}
