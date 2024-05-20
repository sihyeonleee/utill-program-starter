package main.java;

import java.awt.Image;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class Main{
	
	private static final String LOCALPATH = "." + java.io.File.separator + "release" + java.io.File.separator;
    private static final String APPLICATION_NAME = "UtillService";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String ENCODING = "utf-8";
    
    private static JLabel updtState = new JLabel("");

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/client_secret_584987293631-vsuqdjq4sabjaricnba7f00i5ljjpqq9.apps.googleusercontent.com.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = Main.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    
	private static HttpRequestInitializer setHttpTimeout(final HttpRequestInitializer requestInitializer) {
    	return new HttpRequestInitializer() {
    		@Override
    		public void initialize(HttpRequest httpRequest) throws IOException {
    			requestInitializer.initialize(httpRequest);
    			httpRequest.setConnectTimeout(3 * 60000);  // 3 minutes connect timeout
    			httpRequest.setReadTimeout(3 * 60000);  // 3 minutes read timeout
    		}
    	};
	}

    public static void main(String... args) throws IOException  {
    	
		Image img = ImageObj.getBusinessManIconIcon();
		
		// Image img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
		
		// Message Object Create
		TrayIconHandler.registerTrayIcon(img, "Utills", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, updtState);
			}
		});
    	
        // Build a new authorized API client service.
		Drive service = null;
		
		try{
			final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, setHttpTimeout(getCredentials(HTTP_TRANSPORT)))
					.setApplicationName(APPLICATION_NAME)
					.build();
        

	        // 대소 구분 X
	        // 탐색 방법은 https://developers.google.com/drive/api/v3/search-files 참조
        	FileList result = service.files().list()
        			.setSpaces("drive")
        			.setPageSize(1000)
//    	      .setQ("name contains 'aws' and mimeType!='application/vnd.google-apps.folder'")
        			.setQ("name contains 'utillService' and mimeType!='application/vnd.google-apps.folder'")
        			.setFields("nextPageToken, files(id, name)")
        			.execute();
        	
            List<File> files = result.getFiles();
            
            File latestVersion = null;
            
            // Not Connection NetWork OR Exception.. >> Just Run My Default App
            if (files == null || files.isEmpty()) {
            	
            	runUtillService(false);
            	
            } else {
            	
                for (File file : files) {
                	
                	if(latestVersion == null){
                		latestVersion = file;
                	}else {
                		
                		String fileNm = file.getName().substring(0, file.getName().lastIndexOf("."));
                		String latestNm = latestVersion.getName().substring(0, latestVersion.getName().lastIndexOf("."));
                		
            			if(fileNm.compareTo(latestNm) > 0){
                			latestVersion = file;
                		}
                		
                	}
                	
                }
                
                findLastUpdateVersion(service, latestVersion);
                
            }
        }catch (Exception e){
        	TrayIconHandler.displayMessage("Error", e.getMessage(), MessageType.WARNING);
        	runUtillService(false);
        	try {
				Thread.sleep(3000);
				System.exit(0);
			} catch (InterruptedException e1) {
				TrayIconHandler.displayMessage("Error", e1.getMessage(), MessageType.WARNING);
				System.exit(0);
			}
        } 
    }
    
	public static void findLastUpdateVersion(Drive service, File file) throws IOException, InterruptedException{
    	
    	java.io.File path = new java.io.File(LOCALPATH);
    	
    	if(!path.exists()){
    		if(path.mkdirs()){
    			
    		}else {
    			TrayIconHandler.displayMessage("Make Folder Fail", "Again Please", MessageType.WARNING);
    			return;
    		}
    	}
    	
    	if(hasLastVersion(file.getName())){
    		
    		runUtillService(false);
    		
    		System.exit(0);
    		
    	}else {

    		latestUtillDownload(service, file);
    		
        	// Exit This Program
        	Thread.sleep(3000);
        	
        	System.exit(0);
    	}
    	
    }
    
    public static boolean hasLastVersion(String remoteLatest) throws IOException{
    	java.io.File note = new java.io.File(LOCALPATH + "release.lsh");
    	
    	String releaseText = "";
    	
    	if(note.exists()){
    		InputStream in = new FileInputStream(note);
			byte[] buff = new byte[in.available()];
			in.read(buff);
			if(buff != null) {
				try {
					releaseText = new String(buff, ENCODING);
				} catch (UnsupportedEncodingException err) {
					in.close();
				}
			}


	    	String[] releaseList = releaseText.split("\n");
	    	
	    	if(releaseList.length > 0){
	    		String localLatest = releaseList[releaseList.length-1];
	    		
	    		if(remoteLatest.equals(localLatest)){
	    			return true;
	    		}else {
	    			return false;
	    		}
	    	}else {
	    		return false;
	    	}
	    	
    	}else {
    		note.createNewFile();
    		return false;
    	}
    	
    }
    
    public static void latestUtillDownload(Drive service, File file) throws IOException, InterruptedException{
		
		// Update Version Banner
		TrayIconHandler.displayMessage("[LSH_Utill] Has Update Version", "New File Update Please Wait...", MessageType.INFO);
		TrayIconHandler.setToolTip("다운로드 준비중입니다.");
		updtState.setText("다운로드 준비중입니다.");
		
		// Down Load Jar
		OutputStream outputStream = new FileOutputStream(LOCALPATH + "utill.jar", false);
		Drive.Files.Get request = service.files().get(file.getId());
		request.getMediaHttpDownloader().setProgressListener(new DownloadProgressListener()); // .setChunkSize(5000000);
		request.executeMediaAndDownloadTo(outputStream);
		
    	byte[] buff = new byte[1024];
    	outputStream.write(buff);

    	// Run Exec
    	boolean result = runUtillService(false);
    	
    	// Latest Version Record
    	if(result){
    		byte[] buffer = (file.getName() + "\n").getBytes(ENCODING);
    		@SuppressWarnings("resource")
			OutputStream versionWriter = new FileOutputStream(LOCALPATH + "release.lsh", true);
    		versionWriter.write(buffer);
    	}else {
    		
    	}
    	
    	// Update End Banner
    	TrayIconHandler.displayMessage("[LSH_Utill] Update Sucess", "Version Info : " + file.getName() , MessageType.INFO);
    }
    
    public static boolean runUtillService(boolean adminstrator){
    	
    	String fullPath = /*LOCALPATH + */"Core.exe";
    	
    	String cmd = "";
		
		if(adminstrator){
			cmd = "powershell Start-Process powershell -verb runAs" + fullPath;
		}else {
			cmd = "powershell start \'" + fullPath + "\'";
			// cmd = ".\\jre8\\bin\\javaw.exe -jar \'" + fullPath + "\'";
		}
		
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			Map<String, String> result = printStream(proc);
			
			if(!result.get("err").equals("")){
				return false;
			}else {
				return true;
			}
		} catch (IOException | InterruptedException err) {
			err.printStackTrace();
			return false;
		}
    	
    }
    
    private static Map<String, String> printStream(Process process) throws IOException, InterruptedException {
		
		Map<String, String> result = new HashMap<>();
		
		process.waitFor();
		
		try (InputStream psout = process.getInputStream()) {
			
	        BufferedReader reader = new BufferedReader(new InputStreamReader(psout));
	        StringBuilder out = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            out.append(line);
	        }
	        
	        String excuteConsoleLog = out.toString();
	        System.out.println(excuteConsoleLog);
	        result.put("log", excuteConsoleLog);
	        reader.close();
			
		}
		
		try (InputStream psout = process.getErrorStream()) {
			
	        BufferedReader reader = new BufferedReader(new InputStreamReader(psout));
	        StringBuilder out = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            out.append(line);
	        }
	        
	        String errorConsoleLog = out.toString();
	        System.err.println(errorConsoleLog);
	        result.put("err", errorConsoleLog);
	        reader.close();
		}
		
		return result;
		
	}
    
    
    
    
    static class DownloadProgressListener implements MediaHttpDownloaderProgressListener{

        @Override
        public void progressChanged(MediaHttpDownloader downloader) throws IOException {
        	
        	TrayIconHandler.setToolTip( "다운로드 진행률 : " + (int) (downloader.getProgress()*100) + "% 완료" );
        	updtState.setText("다운로드 진행률 : " + (int) (downloader.getProgress()*100) + "% 완료");
        	// one time download size
        	// System.out.println(downloader.getChunkSize());
        	
        	// 1.0 == 100%
        	// System.out.println(downloader.getProgress());
        	
            /*switch (downloader.getDownloadState()){

                //Called when file is still downloading
                //ONLY CALLED AFTER A CHUNK HAS DOWNLOADED,SO SET APPROPRIATE CHUNK SIZE
                case MEDIA_IN_PROGRESS:
                    //Add code for showing progress
                    break;
                //Called after download is complete
                case MEDIA_COMPLETE:
                    //Add code for download completion
                    break;
			default:
				break;
             }*/
        }
        
    }
    
    
    
}