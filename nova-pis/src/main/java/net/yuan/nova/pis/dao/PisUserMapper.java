package net.yuan.nova.pis.dao;

import net.yuan.nova.pis.entity.PisUser;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface PisUserMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PIS_USER
     *
     * @mbggenerated Wed Mar 23 14:22:23 CST 2016
     */
    int deleteByPrimaryKey(String userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PIS_USER
     *
     * @mbggenerated Wed Mar 23 14:22:23 CST 2016
     */
    int insert(PisUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PIS_USER
     *
     * @mbggenerated Wed Mar 23 14:22:23 CST 2016
     */
    int insertSelective(PisUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PIS_USER
     *
     * @mbggenerated Wed Mar 23 14:22:23 CST 2016
     */
    PisUser selectByPrimaryKey(String userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PIS_USER
     *
     * @mbggenerated Wed Mar 23 14:22:23 CST 2016
     */
    int updateByPrimaryKeySelective(PisUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PIS_USER
     *
     * @mbggenerated Wed Mar 23 14:22:23 CST 2016
     */
    int updateByPrimaryKey(PisUser record);
    /**
     * 根据用户ID删除用户信息
     * @param userId
     * @return
     */
    int deleteUserByUserId(String userId);
    /**
     * 根据用户ID删除用户基本信息
     * @param userId
     * @return
     */
    int deletePisUserInfoByUserId(String userId);
    /**
     * 根据用户ID删除用户扩展信息
     * @param userId
     * @return
     */
    int deletePisUserExtendByUserId(String userId);
    /**
     * 根据用户ID删除用户关联信息
     * @param userId
     * @return
     */
    int deletePisUserGroupShipByUserId(String userId);
    /**
     * 根据用户ID删除用户认证信息
     * @param userId
     * @return
     */
    int deletePisUserOauthByUserId(String userId);
    /**
     * 删除用户上传照片附件
     * @param userId
     * @return
     */
    int deletePicUserAttachmentBlob(String userId);
    /**
     * 删除用户上传附件信息
     * @param userId
     * @return
     */
    int  deletePicUserAttachment(String userId);
	// ///////////////////////////////
	// ///// 查詢 ////////
	// ///////////////////////////////

	public PisUser selectUserByUserName(String userName);

	public PisUser selectUserByTel(String tel);

	public PisUser selectUserByEmail(String email);

	/**
	 * 密码重置
	 * 
	 * @param UserId
	 * @param password
	 */
	public int updateUserPwd(PisUser user);
	/**
	 * 得到所有用户
	 * @return
	 */
	public List<PisUser> selectAll();
}