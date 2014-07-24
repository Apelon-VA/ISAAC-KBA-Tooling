/**
 * Copyright Notice
 *
 * This is a work of the U.S. Government and is not subject to copyright
 * protection in the United States. Foreign copyrights may apply.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.va.knowledgeArtifacts.publisher.publish;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import javafx.concurrent.Task;
import org.apache.maven.pom._4_0.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Publish}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class Publish extends Task<Integer>
{
	private static Logger log = LoggerFactory.getLogger(Publish.class);

	Model model_;
	String classifier_;
	File projectFolder_;
	List<File> dataFiles_;
	String url_;
	String username_;
	String password_;
	
	StringBuilder status_ = new StringBuilder();
	
	public Publish(Model model, String classifier, File projectFolder, List<File> dataFiles, String url, String username, String password) throws Exception
	{
		model_ = model;
		classifier_ = classifier;
		projectFolder_ = projectFolder;
		dataFiles_ = dataFiles;
		url_ = url;
		username_ = username;
		password_ = password;
	}

	private void writeChecksumFile(File file, String type, File toFolder, String targetName) throws NoSuchAlgorithmException, IOException
	{
		MessageDigest md = MessageDigest.getInstance(type);
		try (InputStream is = Files.newInputStream(file.toPath()))
		{
			DigestInputStream dis = new DigestInputStream(is, md);
			byte[] buffer = new byte[4096];
			int read = 0;
			while (read != -1)
			{
				read = dis.read(buffer);
			}
		}
		byte[] digest = md.digest();
		String checkSum = new BigInteger(1, digest).toString(16);

		Files.write(new File(toFolder, file.getName() + "." + type.toLowerCase()).toPath(), 
				(checkSum + "  " + (targetName == null ? file.getName() : targetName)).getBytes(), StandardOpenOption.WRITE,
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	private void writeMetadataFile(File toFolder) throws IOException, NoSuchAlgorithmException
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		sb.append("\r\n");
		sb.append("<metadata>");
		sb.append("\r\n");
		sb.append("    <groupId>" + model_.getGroupId() + "</groupId>");
		sb.append("\r\n");
		sb.append("    <artifactId>" + model_.getArtifactId() + "</artifactId>");
		sb.append("\r\n");
		sb.append("    <version>" + model_.getVersion() + "</version>");
		sb.append("\r\n");
		sb.append("</metadata>");
		sb.append("\r\n");
		File file = new File(toFolder, "maven-metadata.xml");
		Files.write(file.toPath(), sb.toString().getBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		writeChecksumFile(file, "MD5", toFolder, null);
		writeChecksumFile(file, "SHA1", toFolder, null);
	}

	private void putFile(File file, String targetFileName) throws Exception
	{
		String groupIdTemp = model_.getGroupId();
		groupIdTemp = groupIdTemp.replaceAll("\\.", "//");
		URL url = new URL(url_ + (url_.endsWith("/") ? "" : "/") + groupIdTemp + "/" + model_.getArtifactId() + "/" + model_.getVersion()
				+ "/" + (targetFileName == null ? file.getName() : targetFileName));

		log.info("Uploading " + file.getAbsolutePath() + " to " + url.toString());

		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		if (username_.length() > 0 || password_.length() > 0)
		{
			String encoded = Base64.getEncoder().encodeToString((username_ + ":" + password_).getBytes());
			httpCon.setRequestProperty("Authorization", "Basic " + encoded);
		}
		httpCon.setDoOutput(true);
		httpCon.setRequestMethod("PUT");
		httpCon.setConnectTimeout(30 * 1000);
		httpCon.setReadTimeout(60 * 60 * 1000);
		httpCon.setChunkedStreamingMode(8192);
		//httpCon.setFixedLengthStreamingMode(file.length());
		OutputStream out = httpCon.getOutputStream();

		byte[] buf = new byte[8192];
		FileInputStream fis = new FileInputStream(file);
		int read = 0;
		while ((read = fis.read(buf, 0, buf.length)) > 0)
		{
			out.write(buf, 0, read);
		}
		out.flush();
		out.close();
		fis.close();
		InputStream is = httpCon.getInputStream();
		StringBuilder sb = new StringBuilder();
		read = 0;
		byte[] buffer = new byte[1024];
		CharBuffer cBuffer = ByteBuffer.wrap(buffer).asCharBuffer();
		while (read != -1)
		{
			read = is.read(buffer);
			if (read > 0)
			{
				sb.append(cBuffer, 0, read);
			}
		}
		httpCon.disconnect();
		if (sb.toString().trim().length() > 0)
		{
			throw new Exception("The server reported an error during the publish operation:  " + sb.toString());
		}
		log.info("Upload Successful");
	}

	/**
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected Integer call() throws Exception
	{
		//TODO wire the progress bar to real progress on zip and upload...
		updateProgress(-1, 50);
		updateStatus("Creating Archive File");
		Zip zip = new Zip();
		File zipFile = zip.createZipFile(model_, classifier_, projectFolder_, dataFiles_);
		log.info("Wrote " + zipFile);

		File pomFile = new File(projectFolder_, "pom.xml");

		updateStatus("Creating Checksum Files");
		writeChecksumFile(pomFile, "MD5", zipFile.getParentFile(), model_.getName() + "-" + model_.getVersion() + ".pom.md5");
		writeChecksumFile(pomFile, "SHA1", zipFile.getParentFile(), model_.getName() + "-" + model_.getVersion() + ".pom.sha1");
		writeChecksumFile(zipFile, "MD5", zipFile.getParentFile(), null);
		writeChecksumFile(zipFile, "SHA1", zipFile.getParentFile(), null);
		updateStatus("Creating Metadata File");
		writeMetadataFile(zipFile.getParentFile());

		updateStatus("Uploading data files");
		putFile(zipFile, null);
		putFile(new File(zipFile.getParentFile(), zipFile.getName() + ".md5"), null);
		putFile(new File(zipFile.getParentFile(), zipFile.getName() + ".sha1"), null);
		
		updateStatus("Uploading pom files");
		putFile(pomFile, model_.getName() + "-" + model_.getVersion() + ".pom");
		putFile(new File(zipFile.getParentFile(), "pom.xml.md5"), model_.getName() + "-" + model_.getVersion() + ".pom.md5");
		putFile(new File(zipFile.getParentFile(), "pom.xml.sha1"), model_.getName() + "-" + model_.getVersion() + ".pom.sha1");
		
		updateStatus("Uploading metadata files");
		putFile(new File(zipFile.getParentFile(), "maven-metadata.xml"), null);
		putFile(new File(zipFile.getParentFile(), "maven-metadata.xml.md5"), null);
		putFile(new File(zipFile.getParentFile(), "maven-metadata.xml.sha1"), null);

		log.debug("Cleaning up temp files");
		Files.walkFileTree(zipFile.getParentFile().toPath(), new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
			{
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
		return 0;
	}
	
	private void updateStatus(String message)
	{
		status_.append(message + "\r\n");
		updateMessage(status_.toString());
	}
}
