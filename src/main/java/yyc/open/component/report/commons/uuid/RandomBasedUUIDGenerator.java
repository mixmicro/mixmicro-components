package yyc.open.component.report.commons.uuid;

import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

/**
 * {@link RandomBasedUUIDGenerator}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2021/8/2
 */
class RandomBasedUUIDGenerator implements UUIDGenerator {
    /**
     * Returns a Base64 encoded version of a Version 4.0 compatible UUID
     * as defined here: http://www.ietf.org/rfc/rfc4122.txt
     */
    @Override
    public String getBase64UUID() {
        return getBase64UUID(SecureRandomHolder.INSTANCE);
    }

    /**
     * Returns a Base64 encoded {@link SecureString} of a Version 4.0 compatible UUID
     * as defined here: http://www.ietf.org/rfc/rfc4122.txt
     */
    public SecureString getBase64UUIDSecureString() {
        byte[] uuidBytes = null;
        byte[] encodedBytes = null;
        try {
            uuidBytes = getUUIDBytes(SecureRandomHolder.INSTANCE);
            encodedBytes = Base64.getUrlEncoder().withoutPadding().encode(uuidBytes);
            return new SecureString(CharArrays.utf8BytesToChars(encodedBytes));
        } finally {
            if (uuidBytes != null) {
                Arrays.fill(uuidBytes, (byte) 0);
            }
            if (encodedBytes != null) {
                Arrays.fill(encodedBytes, (byte) 0);
            }
        }
    }

    /**
     * Returns a Base64 encoded version of a Version 4.0 compatible UUID
     * randomly initialized by the given {@link Random} instance
     * as defined here: http://www.ietf.org/rfc/rfc4122.txt
     */
    public String getBase64UUID(Random random) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(getUUIDBytes(random));
    }

    private byte[] getUUIDBytes(Random random) {
        final byte[] randomBytes = new byte[16];
        random.nextBytes(randomBytes);
        /* Set the version to version 4 (see http://www.ietf.org/rfc/rfc4122.txt)
         * The randomly or pseudo-randomly generated version.
         * The version number is in the most significant 4 bits of the time
         * stamp (bits 4 through 7 of the time_hi_and_version field).*/
        randomBytes[6] &= 0x0f;  /* clear the 4 most significant bits for the version  */
        randomBytes[6] |= 0x40;  /* set the version to 0100 / 0x40 */

        /* Set the variant:
         * The high field of th clock sequence multiplexed with the variant.
         * We set only the MSB of the variant*/
        randomBytes[8] &= 0x3f;  /* clear the 2 most significant bits */
        randomBytes[8] |= 0x80;  /* set the variant (MSB is set)*/
        return randomBytes;
    }
}
