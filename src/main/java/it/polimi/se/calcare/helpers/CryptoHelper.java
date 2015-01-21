/*
 * Copyright (C) 2015 ff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.polimi.se.calcare.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.Stateless;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author ff
 */
@Stateless
public class CryptoHelper {
    
    private final byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private final IvParameterSpec ivSpec;

    public CryptoHelper() {
        this.ivSpec = new IvParameterSpec(iv);
    }
    
    private byte[] doAES(int opmode, byte[] input) {
        byte[] key = Arrays.copyOfRange(JWTHelper.SECRET.getBytes(), 0, 16);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(CryptoHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            cipher.init(opmode, keySpec, ivSpec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(CryptoHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            return cipher.doFinal(input);
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(CryptoHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    public byte[] encrypt(byte[] input) {
        return doAES(Cipher.ENCRYPT_MODE, input);
    }
    
    public byte[] decrypt(byte[] input) {
        return doAES(Cipher.DECRYPT_MODE, input);
    }
    
    public byte[] objToBytes(Object obj) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        return bos.toByteArray();
    }
    
    public Object bytesToObj(byte[] bytes) throws IOException, ClassNotFoundException{
        ByteArrayInputStream bos = new ByteArrayInputStream(bytes);
        ObjectInputStream oos = new ObjectInputStream(bos);
        return oos.readObject();
    }
    
    public String bytesToHex(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }
    
    public byte[] hexToBytes(String str) {
        return DatatypeConverter.parseHexBinary(str);
    }
}
