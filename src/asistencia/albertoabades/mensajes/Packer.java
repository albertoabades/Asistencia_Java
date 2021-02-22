package asistencia.albertoabades.mensajes;

public class Packer {
	
	public static byte[] packInt(int number) {
		byte b0 = (byte)((number >> 24) & 0xFF); // i0
		byte b1 = (byte)((number >> 16) & 0xFF); // i1
		byte b2 = (byte)((number >> 8) & 0xFF); // i2
		byte b3 = (byte)((number) & 0xFF); // i3
		
		return new byte[]{b3, b2, b1, b0};
	}
	
	public static int unpackInt(byte[] bytes) {
		int i0 = ((bytes[3] & 0xFF) << 24); // b0 
		int i1 = ((bytes[2] & 0xFF) << 16); // b1
		int i2 = ((bytes[1] & 0xFF) << 8); // b2
		int i3 = ((bytes[0] & 0xFF)); // b3
		
		return i0 | i1 | i2 | i3;
	}
	
	public static byte[] packLong(long number) {
		byte b0 = (byte)((number >> 56) & 0xFF); // l0
		byte b1 = (byte)((number >> 48) & 0xFF); // l1
		byte b2 = (byte)((number >> 40) & 0xFF); // l2
		byte b3 = (byte)((number >> 32) & 0xFF); // l3
		byte b4 = (byte)((number >> 24) & 0xFF); // l4
		byte b5 = (byte)((number >> 16) & 0xFF); // l5
		byte b6 = (byte)((number >> 8) & 0xFF); // l6
		byte b7 = (byte)((number) & 0xFF); // l7
		
		return new byte[]{b7, b6, b5, b4, b3, b2, b1, b0};
	}
	
	public static long unpackLong(byte[] bytes) {
		long low = 
				((bytes[0] & 0xff) << 0) +
				((bytes[1] & 0xff) << 8) +
				((bytes[2] & 0xff) << 16) +
				((bytes[3] & 0xff) << 24);
		long high = 
				((bytes[4] & 0xff) << 0) +
				((bytes[5] & 0xff) << 8) +
				((bytes[6] & 0xff) << 16) +
				((bytes[7] & 0xff) << 24);
		return (high << 32) + (0xffffffffL & low); 
	}

}

