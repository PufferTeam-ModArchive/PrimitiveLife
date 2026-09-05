package net.pufferlab.primal.utils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.pufferlab.primal.Primal;

import org.apache.commons.io.FileUtils;

import com.google.gson.*;

import io.netty.buffer.ByteBuf;

public class IOUtils {

    public static File getResourceDir() {
        File rpDir = new File(Launch.minecraftHome, "resourcepacks");

        if (!rpDir.exists()) rpDir.mkdirs();
        return rpDir;
    }

    public static File getStructureDir() {
        File rpDir = new File(Launch.minecraftHome, "structures");

        if (!rpDir.exists()) rpDir.mkdirs();
        return rpDir;
    }

    public static File getConfigDir() {
        File rpDir = new File(Launch.minecraftHome, "config");

        if (!rpDir.exists()) rpDir.mkdirs();
        return rpDir;
    }

    public static File createResourceFile(String name, String extension) throws IOException {
        return new File(getResourceDir(), name + "." + extension);
    }

    public static File createStructureFile(String name, String extension) {
        try {
            return new File(getStructureDir(), name + "." + extension);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static File createConfigFile(String name) {
        return new File(getConfigDir(), name + ".cfg");
    }

    public static File createTempFile() {
        try {
            return File.createTempFile("tmp_" + UUID.randomUUID() + "_", ".tmp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File createNamedTempFile(String name, String extension) {
        return new File(System.getProperty("java.io.tmpdir"), name + "." + extension);
    }

    public static InputStream createStream(String resourceStream) {
        return Primal.class.getResourceAsStream(resourceStream);
    }

    public static File createResourceStreamFile(String resourceStream, String name, String extension) {
        File temp = IOUtils.createNamedTempFile(name, extension);

        try {
            InputStream stream = createStream(resourceStream);
            copyInputStream(stream, temp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return temp;
    }

    public static String readFile(File file) throws IOException {
        return file.exists() ? FileUtils.readFileToString(file, "UTF-8")
            .trim() : null;
    }

    public static void writeFile(File file, String content) throws IOException {
        FileUtils.writeStringToFile(file, content.trim(), "UTF-8");
    }

    public static void copyFile(File from, File to) throws IOException {
        FileUtils.copyFile(from, to);
    }

    public static void copyInputStream(InputStream in, File file) throws IOException {
        Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void writeNBTFile(File file, NBTTagCompound nbt) {
        try {
            CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static NBTTagCompound readNBTFile(InputStream stream) {
        try {
            return CompressedStreamTools.readCompressed(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static NBTTagCompound readNBTFile(String stream) {
        return readNBTFile(IOUtils.createStream(stream));
    }

    public static NBTTagCompound readNBTFile(File file) {
        try {
            return readNBTFile(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String writeNBT(NBTTagCompound nbt) {
        try {
            return nbt.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static NBTTagCompound readNBT(String string) {
        try {
            return (NBTTagCompound) JsonToNBT.func_150315_a(string);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static final Gson gson = new GsonBuilder().setPrettyPrinting()
        .create();

    public static void writeJSON(File file, JsonObject object) {
        try {
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(object, writer);
            } catch (JsonIOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static JsonObject readJSON(File file) {
        try {
            try (FileReader reader = new FileReader(file)) {
                JsonObject object = new JsonParser().parse(reader)
                    .getAsJsonObject();
                return object;
            } catch (JsonIOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void downloadFile(String urlTxt, String extension, File out) throws IOException {
        try {
            URL url = new URL(urlTxt + "." + extension);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (InputStream in = connection.getInputStream()) {
                FileUtils.copyInputStreamToFile(in, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String sha256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }

        byte[] hash = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void writeString(ByteBuf buf, String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length); // write length first
        buf.writeBytes(bytes); // then write bytes
    }

    public static void writeIntArray(ByteBuf buf, int[] array) {
        buf.writeInt(array.length);
        for (int i = 0; i < array.length; i++) {
            buf.writeInt(array[i]);
        }
    }

    public static String readString(ByteBuf buf) {
        int length = buf.readInt(); // first read length
        byte[] bytes = new byte[length];
        buf.readBytes(bytes); // then read bytes
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static int[] readIntArray(ByteBuf buf) {
        int length = buf.readInt();
        int[] array = new int[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = buf.readInt();
        }
        return array;
    }

    public static void writeAABBList(ByteBuf buf, List<AxisAlignedBB> list) {
        buf.writeInt(list.size());
        for (AxisAlignedBB bb : list) {
            writeAABB(buf, bb);
        }
    }

    public static List<AxisAlignedBB> readAABBList(ByteBuf buf) {
        int length = buf.readInt();
        List<AxisAlignedBB> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            AxisAlignedBB bb = readAABB(buf);
            list.add(bb);
        }
        return list;
    }

    public static void writeAABB(ByteBuf buf, AxisAlignedBB bb) {
        buf.writeFloat((float) bb.minX);
        buf.writeFloat((float) bb.minY);
        buf.writeFloat((float) bb.minZ);
        buf.writeFloat((float) bb.maxX);
        buf.writeFloat((float) bb.maxY);
        buf.writeFloat((float) bb.maxZ);
    }

    public static void writeBB(ByteBuf buf, BoundingBox bb) {
        buf.writeFloat((float) bb.minX);
        buf.writeFloat((float) bb.minY);
        buf.writeFloat((float) bb.minZ);
        buf.writeFloat((float) bb.maxX);
        buf.writeFloat((float) bb.maxY);
        buf.writeFloat((float) bb.maxZ);
        buf.writeFloat(bb.rotation.x);
        buf.writeFloat(bb.rotation.y);
        buf.writeFloat(bb.rotation.z);
    }

    public static AxisAlignedBB readAABB(ByteBuf buf) {
        double minX = buf.readFloat();
        double minY = buf.readFloat();
        double minZ = buf.readFloat();
        double maxX = buf.readFloat();
        double maxY = buf.readFloat();
        double maxZ = buf.readFloat();
        return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static BoundingBox readBB(ByteBuf buf) {
        double minX = buf.readFloat();
        double minY = buf.readFloat();
        double minZ = buf.readFloat();
        double maxX = buf.readFloat();
        double maxY = buf.readFloat();
        double maxZ = buf.readFloat();
        float rotX = buf.readFloat();
        float rotY = buf.readFloat();
        float rotZ = buf.readFloat();
        BoundingBox bb = BoundingBox.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        bb.setRotation(rotX, rotY, rotZ);
        return bb;
    }
}
