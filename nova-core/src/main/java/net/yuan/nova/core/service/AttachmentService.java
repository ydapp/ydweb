package net.yuan.nova.core.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.coobird.thumbnailator.Thumbnails;
import net.yuan.nova.commons.SystemConstant;
import net.yuan.nova.core.dao.AttachmentDao;
import net.yuan.nova.core.entity.Attachment;
import net.yuan.nova.core.entity.AttachmentBlob;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Service
public class AttachmentService {

	protected final Logger log = LoggerFactory.getLogger(AttachmentService.class);

	@Autowired
	private AttachmentDao attachmentDao;
	@Autowired
	private AttachmentBlobService attachmentBlobService;

	/**
	 * @param multipartFile
	 *            附件文件
	 * @param kindId
	 *            业务主键
	 * @param tableName
	 *            对应的业务表名
	 * @return
	 */
	public Attachment addUploadFile(MultipartFile multipartFile, String kindId, Attachment.TableName tableName) {
		return this.addUploadFile(multipartFile, multipartFile.getOriginalFilename(), kindId, tableName,
				Attachment.State.A);
	}

	/**
	 * 上传附件
	 * 
	 * @param multipartFile
	 *            附件文件
	 * @param fileName
	 *            附件的文件名称
	 * @param kindId
	 *            业务主键
	 * @param tableName
	 *            对应的业务表名
	 * @param state
	 *            附件的状态
	 * @return
	 */
	public Attachment addUploadFile(MultipartFile multipartFile, String fileName, String kindId,
			Attachment.TableName tableName, Attachment.State state) {
		Attachment.Type type = getFileType(fileName);
		String uploadPath = getUploadPath(fileName);
		Attachment attachment = new Attachment();
		attachment.setAppAtchId(UUID.randomUUID().toString());
		attachment.setKindId(kindId);
		attachment.setTableName(tableName);
		attachment.setComments("");
		attachment.setAtchName(fileName);
		attachment.setSavePath(uploadPath);
		attachment.setActhType(type);
		attachment.setActhSize(multipartFile.getSize());
		byte[] fileBytes = null;
		try {
			fileBytes = multipartFile.getBytes();
			// 将文件保存到文件系统
			String filePath = System.getProperty(SystemConstant.WEBAPP_ROOT) + uploadPath;
			multipartFile.transferTo(new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		attachment.setMd5(DigestUtils.md5Hex(fileBytes));
		attachment.setState(state);
		attachmentDao.addAttachment(attachment);
		// 保存文件到数据库和磁盘目录
		this.saveBlob(attachment, fileBytes);
		return attachment;
	}
	
	
	/**
	 * 上传附件
	 * 
	 * @param multipartFile
	 *            附件文件
	 * @param fileName
	 *            附件的文件名称
	 * @param kindId
	 *            业务主键
	 * @param tableName
	 *            对应的业务表名
	 * @param state
	 *            附件的状态
	 * @return
	 */
	public Attachment addUploadFile(MultipartFile multipartFile, String fileName, String kindId,
			Attachment.TableName tableName) {
		Attachment.Type type = getFileType(fileName);
		String uploadPath = getUploadPath(fileName);
		Attachment attachment = new Attachment();
		attachment.setAppAtchId(UUID.randomUUID().toString());
		attachment.setKindId(kindId);
		attachment.setTableName(tableName);
		attachment.setComments("");
		attachment.setAtchName(fileName);
		attachment.setSavePath(uploadPath);
		attachment.setActhType(type);
		attachment.setActhSize(multipartFile.getSize());
		byte[] fileBytes = null;
		try {
			fileBytes = multipartFile.getBytes();
			// 将文件保存到文件系统
			String filePath = System.getProperty(SystemConstant.WEBAPP_ROOT) + uploadPath;
			//multipartFile.transferTo(new File(filePath));
	        CommonsMultipartFile cf= (CommonsMultipartFile)multipartFile; 
	        DiskFileItem fi = (DiskFileItem)cf.getFileItem(); 
	        File file = fi.getStoreLocation(); 
			Thumbnails.of(file).scale(0.1f).toFile(new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		attachment.setMd5(DigestUtils.md5Hex(fileBytes));
		attachment.setState(Attachment.State.A);
		attachmentDao.addAttachment(attachment);
		// 保存文件到数据库和磁盘目录
		this.saveBlob(attachment, fileBytes);
		return attachment;
	}

	public Attachment addUploadFile(File file, String fileName, String kindId, Attachment.TableName tableName,
			Attachment.State state) {
		Attachment.Type type = getFileType(fileName);
		String uploadPath = getUploadPath(fileName);
		Attachment attachment = new Attachment();
		attachment.setAppAtchId(UUID.randomUUID().toString());
		attachment.setKindId(kindId);
		attachment.setTableName(tableName);
		attachment.setComments("");
		attachment.setAtchName(fileName);
		attachment.setSavePath(uploadPath);
		attachment.setActhType(type);
		attachment.setActhSize(file.length());
		byte[] fileBytes = null;
		try {
			fileBytes = FileUtils.readFileToByteArray(file);
			// 将文件保存到文件系统
			String filePath = System.getProperty(SystemConstant.WEBAPP_ROOT) + uploadPath;
			//FileUtils.copyFileToDirectory(file,new File(filePath));
			//FileUtils.moveFile(file, new File(filePath));
			FileUtils.copyFile(file, new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		attachment.setMd5(DigestUtils.md5Hex(fileBytes));
		attachment.setState(state);
		attachmentDao.addAttachment(attachment);
		// 保存文件到数据库和磁盘目录
		this.saveBlob(attachment, fileBytes);
		return attachment;
	}
	 
	/**
	 * 替换文件名
	 */
	public String doChangeFileName(MultipartFile file, String name) {
		String  fileName = file.getOriginalFilename();//��ȡ�ļ�����
		int fileNameLen = fileName.lastIndexOf(".");//��ȡ��׺��.��λ��
		String fileExtension=fileName.substring(fileNameLen, fileName.length());//��ȡ��׺��
		return name+""+fileExtension;//����������
	}

	private Attachment.Type getFileType(String fileName) {
		Attachment.Type type = Attachment.Type.P;
		if (Attachment.isFileType(fileName, Attachment.PHOTO_TYPE)) {
			// 图片
			type = Attachment.Type.P;
		} else if (Attachment.isFileType(fileName, Attachment.FILE_TYPE)) {
			// 文件
			type = Attachment.Type.F;
		} else if (Attachment.isFileType(fileName, Attachment.AUDIO_TYPE)) {
			// 音频
			type = Attachment.Type.A;
		} else if (Attachment.isFileType(fileName, Attachment.VIDEO_TYPE)) {
			// 视频
			type = Attachment.Type.V;
		} else {
			type = Attachment.Type.O;
		}
		return type;
	}

	/**
	 * 保存文件到数据库和磁盘目录
	 * 
	 * @param appAtchId
	 * @param multipartFile
	 * @param uploadPath
	 */
	private void saveBlob(Attachment attachment, byte[] fileBytes) {
		// 判断文件在数据库中是否已存在
		if (attachmentBlobService.exists(attachment.getMd5())) {
			log.info("文件已经在数据库中存在");
		} else {
			// 将附件文件保存到数据库中
			attachmentBlobService.addUploadFile(attachment.getMd5(), attachment.getActhSize(), fileBytes);
		}
	}

	/**
	 * 更新附件对应的业务主键
	 * 
	 * @param attachment
	 * @return
	 */
	public boolean updateKindId(Attachment attachment) {
		return attachmentDao.updateAttachment(attachment) > 0;
	}

	/**
	 * 更新附件对应的业务主键
	 * 
	 * @param kindId
	 * @param kind
	 * @param appAtchIds
	 * @return
	 */
	@Transactional
	public int[] updateKindId(String kindId, Attachment.TableName kind, String... appAtchIds) {
		if (appAtchIds == null) {
			return null;
		}
		int[] result = new int[appAtchIds.length];
		Attachment attachment = new Attachment();
		attachment.setKindId(kindId);
		attachment.setTableName(kind);
		for (int i = 0; i < appAtchIds.length; i++) {
			attachment.setAppAtchId(appAtchIds[i]);
			result[i] = attachmentDao.updateAttachment(attachment);
		}
		return result;
	}

	/**
	 * 获得某笔业对应的所有附件
	 * 
	 * @param kindId
	 * @param kind
	 * @param state
	 * @return
	 */
	public List<Attachment> getAttachmentsByKindId(String kindId, Attachment.TableName kind, Attachment.State state) {
		return attachmentDao.getAttachmentsByKindId(kindId, kind, state);
	}

	/**
	 * 根据附件ID获得附件
	 * 
	 * @param appAtchId
	 * @return
	 */
	public Attachment getAttachmentById(String appAtchId) {
		return attachmentDao.getAttachmentById(appAtchId);
	}

	/**
	 * 根据附件ID删除附件
	 * 
	 * @param appAtchId
	 */
	public int deleteAttachment(String appAtchId) {
		return this.attachmentDao.deleteAttachment(appAtchId);
	}

	/**
	 * 删除某笔业对应的所有附件
	 * 
	 * @param kindId
	 * @param tableName
	 */
	public void deleteAttachmentsByKindId(String kindId, Attachment.TableName tableName) {
		attachmentDao.deleteAttachmentsByKindId(kindId, tableName);
	}

	/**
	 * 将附件转存到磁盘中
	 * 
	 * @param attachment
	 * @return
	 */
	public File transferToFile(Attachment attachment) {
		// 获得文件路径
		String path = attachment.getSavePath();
		String downLoadPath = System.getProperty(SystemConstant.WEBAPP_ROOT) + path;
		File file = new File(downLoadPath);
		if (!file.exists()) {
			// 文件不存在，将附件转存到磁盘中
			AttachmentBlob blob = attachmentBlobService.findAttachmentBlob(attachment.getMd5());
			if (blob == null) {
				return null;
			}
			byte[] blobByte = blob.getAtchCont();
			BufferedOutputStream bos = null;
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				bos = new BufferedOutputStream(fos);
				bos.write(blobByte);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(bos);
				IOUtils.closeQuietly(fos);
			}
		}
		return file;
	}

	/**
	 * 生成附件对应的缩略图，没有则返回null
	 * 
	 * @param attachment
	 * @param size
	 *            大小
	 * @return 缩略图在磁盘上的相对路径
	 * @throws IOException
	 */
	public String thumbnailator(Attachment attachment, int size) throws IOException {
		if (attachment == null || !Attachment.Type.P.equals(attachment.getActhType())) {
			// 为空或不是图片
			return null;
		}
		String savePath = attachment.getSavePath();
		String path = System.getProperty(SystemConstant.WEBAPP_ROOT) + savePath;
		File file = new File(path);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file = this.transferToFile(attachment);
		}
		if (file == null || !file.exists()) {
			return null;
		}
		// 新文件的保存路径
		String newSavePath = savePath.substring(0, savePath.lastIndexOf('.')) + "_" + size
				+ getFileExt(attachment.getAtchName());

		File newFile = new File(System.getProperty(SystemConstant.WEBAPP_ROOT) + newSavePath);
		// 缩略图不存在的，生成缩略图
		if (!newFile.exists()) {
			// 缩放并压缩图片
			log.debug("生成缩略图");
			//Thumbnails.of(file).size(size, size).outputQuality(0.8f).toFile(newFile);
			Thumbnails.of(file).scale(0.6f).toFile(newFile);
		}
		return newSavePath;
	}

	private SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd");

	/**
	 * 得到文件上传的相对路径
	 * 
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public String getUploadPath(String fileName) {
		String uploadPath = new StringBuffer(SystemConstant.UPLOAD_FOLDER).append("/")
				.append(formater.format(new Date())).append("/").append(System.currentTimeMillis())
				.append(getFileExt(fileName)).toString();
		// 判断父文件夹是否存在，不存在则创建
		File dir = new File(System.getProperty(SystemConstant.WEBAPP_ROOT) + uploadPath).getParentFile();
		if (!dir.exists()) {
			try {
				// 文件不存在，创建所在的文件夹
				dir.mkdirs();
			} catch (Exception e) {
				log.error("创建附件目录时出错", e);
				return "";
			}
		}
		return uploadPath;
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @return string
	 */
	public static String getFileExt(String fileName) {
		return fileName.substring(fileName.lastIndexOf("."));
	}

	/**
	 * 还原附件到文件
	 * 
	 * @return 返回最后同步的文件的创建时间
	 */
	public String restoreAttachment(String attachmentId) {
		int pageNum = 1;
		int rows = 20;
		Attachment attachment = null;
		boolean finished = false;
		do {
			// 获得id和路径的列表，遍历并判断是否需要写入文件
			List<Attachment> list = this.getAttachmentList(pageNum++, rows, attachmentId);
			int size = list.size();
			if (size == 0) {
				// 没有记录了，停止当前循环
				break;
			}
			finished = size < rows;
			// 同步当前文件
			for (int i = 0; i < size; i++) {
				attachment = list.get(i);
				// 路径
				String path = attachment.getSavePath();
				String filePath = System.getProperty(SystemConstant.WEBAPP_ROOT) + path;
				log.debug("同步文件：" + filePath);
				File file = new File(filePath);
				if (file.exists()) {
					log.debug("文件已存在，跳过");
					continue;
				}
				// 文件不存在
				try {
					// 需要写入文件的附件，获得完整附件数据
					AttachmentBlob blob = attachmentBlobService.findAttachmentBlob(attachment.getMd5());
					if (blob != null) {
						FileUtils.writeByteArrayToFile(file, blob.getAtchCont());
						log.debug("文件写入成功");
					} else {
						log.debug("没有相应的附件文件");
					}
				} catch (IOException e) {
					log.error("写入文件出错", e);
				} catch (Exception e) {
					log.error("其他的一些错误", e);
				}
			}
		} while (!finished);
		if (attachment != null) {
			return attachment.getAppAtchId();
		}
		return attachmentId;
	}

	private List<Attachment> getAttachmentList(int pageNum, int rows, String attachmentId) {
		int offset = (pageNum - 1) * rows;
		int endRowNum = offset + rows;
		List<Attachment> list = attachmentDao.getAttachmentList(offset, endRowNum, attachmentId);
		return list;
	}

	/**
	 * 清理无效的附件
	 */
	@Async
	public void cleanAttachment(Attachment.TableName tableName) {
		attachmentDao.cleanAttachment(tableName);
		attachmentBlobService.cleanAttachmentBlob();
	}
}
