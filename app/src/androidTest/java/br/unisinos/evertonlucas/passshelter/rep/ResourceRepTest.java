package br.unisinos.evertonlucas.passshelter.rep;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import javax.crypto.SecretKey;

import br.unisinos.evertonlucas.passshelter.encryption.SymmetricEncryption;
import br.unisinos.evertonlucas.passshelter.model.Resource;
import br.unisinos.evertonlucas.passshelter.util.KeyGenerationUtil;

/**
 * Class created to test ResourceRep
 * Created by everton on 31/08/15.
 */
public class ResourceRepTest extends AndroidTestCase {

    private static final String TEST_PREFIX = "test_";
    private ResourceRep resourceRep;
    private SymmetricEncryption encryption;
    private String resName = "Facebook";
    private String user = "User";
    private String password = "123456";

    public void setUp() throws Exception {
        super.setUp();

        final SecretKey key = KeyGenerationUtil.generate();
        this.encryption = new SymmetricEncryption(key);

        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), TEST_PREFIX);
        resourceRep = new ResourceRep(context, encryption);
    }

    public void tearDown() throws Exception {
        super.tearDown();

        resourceRep = null;
    }

    public void testInsertResource() throws Exception {
        Resource resource = getResource(resName, this.user, password);
        resourceRep.insertResource(resource);
        Resource resQuery = resourceRep.getResourceByName(this.resName);
        assertNotNull(resQuery);
        assertNotNull(resQuery.getId());
        assertEquals(this.resName, resQuery.getName());
        assertEquals(this.user, resQuery.getUser());
        assertEquals(this.password, resQuery.getPassword());
    }

    private Resource getResource(String facebook, String user, String password) {
        Resource resource = new Resource(encryption);
        resource.setName(facebook);
        resource.setUser(user);
        resource.setPassword(password);
        return resource;
    }
}