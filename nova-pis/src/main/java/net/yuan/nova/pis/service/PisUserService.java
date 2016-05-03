package net.yuan.nova.pis.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.yuan.nova.core.shiro.PasswordHelper;
import net.yuan.nova.core.shiro.vo.User;
import net.yuan.nova.pis.dao.PisUserMapper;
import net.yuan.nova.pis.entity.PisUser;
import net.yuan.nova.pis.entity.PisUserGroup;
import net.yuan.nova.pis.entity.PisUserGroupShipKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PisUserService {

	@Autowired
	private PisUserMapper pisUserMapper;
	@Autowired
	private PasswordHelper passwordHelper;
	@Autowired
	private UserGroupShipKeyService keyService;
	@Autowired
	private UserGroupService groupService;

	// ///////////////////////////////
	// ///// 查詢 ////////
	// ///////////////////////////////

	@Cacheable(value = "userCache", key = "'findUserById_'+#userId")
	public PisUser findUserById(String userId) {
		return pisUserMapper.selectByPrimaryKey(userId);
	}

	@Cacheable(value = "userCache", key = "'findUserByUserName_'+#userName")
	public PisUser findUserByUserName(String userName) {
		return pisUserMapper.selectUserByUserName(userName);
	}

	@Cacheable(value = "userCache", key = "'findUserByTel_'+#tel")
	public PisUser findUserByTel(String tel) {
		return pisUserMapper.selectUserByTel(tel);
	}

	@Cacheable(value = "userCache", key = "'findUserByEmail_'+#email")
	public PisUser findUserByEmail(String email) {
		return pisUserMapper.selectUserByEmail(email);
	}

	/**
	 * 新增用户
	 * 
	 * @param user
	 */
	@CacheEvict(value = "userCache", allEntries = true)
	public int insertUser(PisUser pisUser) {
		if (pisUser == null) {
			return 0;
		}
		// 验证手机号码、邮箱等有没有被使用

		pisUser.setUserId(UUID.randomUUID().toString());
		// 对密码进行加密
		User user = passwordHelper.encryptPassword(pisUser.toUser());
		pisUser.setPassword(user.getPassword());
		pisUser.setSalt(user.getSalt());
		pisUser.setCreateTime(new Date());
		return pisUserMapper.insertSelective(pisUser);
		// return pisUserMapper.insertUser(pisUser);
	}

	/**
	 * 修改个人信息
	 * 
	 * @param user
	 */
	@CacheEvict(value = "userCache", allEntries = true)
	public int updateUser(PisUser user) {
		return pisUserMapper.updateByPrimaryKeySelective(user);
	}

	/**
	 * 密码重置
	 * 
	 * @param UserId
	 * @param password
	 */
	@CacheEvict(value = "userCache", allEntries = true)
	public int updateUserPwd(PisUser user) {
		return pisUserMapper.updateUserPwd(user);
	}

	// ///////////////////////////////
	// ///// 用户组操作 ////////
	// ///////////////////////////////

	/**
	 * 根据用户id得到用户所在组
	 * 
	 * @param userId
	 * @return
	 */
	public List<PisUserGroup> getUserGroup(String userId) {
		List<PisUserGroupShipKey> list = this.keyService.getByUserId(userId);
		if (list != null) {
			List<PisUserGroup> groupList = new ArrayList<PisUserGroup>();
			for (PisUserGroupShipKey key : list) {
				PisUserGroup group = this.groupService.getByGroupId(key.getGroupId());
				if (group != null) {
					groupList.add(group);
				}
			}
			return groupList;
		}
		return null;
	}

	/**
	 * 根据用户id得到用户所在组的类型
	 * 
	 * @param userId
	 * @return
	 */
	public PisUserGroup getPisUserGroup(String userId) {
		List<PisUserGroupShipKey> list = this.keyService.getByUserId(userId);
		if (list != null && list.size() > 0) {
			PisUserGroupShipKey pisUserGroupShipKey = list.get(0);
			return this.groupService.getByGroupId(pisUserGroupShipKey.getGroupId());
		}
		return null;
	}
	public List<PisUser> getAll(){
		return this.pisUserMapper.selectAll();
	}
	
	/**
	 * 验证密码是否正确
	 * @param pisUser 用户实体类
	 * @return true:原密码正确,false：原密码错误
	 */
	public boolean  checkPassword(String pwd,String loginName){
		PisUser picUser = pisUserMapper.selectUserByUserName(loginName);
		String password = this.passwordHelper.encryptPassword(pwd,picUser.getSalt());
		return picUser.getPassword().equals(password);
	}
}
