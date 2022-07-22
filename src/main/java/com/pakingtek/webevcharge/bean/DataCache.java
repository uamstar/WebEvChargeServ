package com.pakingtek.webevcharge.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;

public enum DataCache {
	INSTANCE;
	
	public Cache userCache;
	private List<User> userList = new ArrayList<User>();
	
	DataCache(){
		CacheManager manager = CacheManager.newInstance();
		this.userCache = manager.getCache("users");
	}
	
	public void addUser(User user) {
		Element element = new Element(user.getUserId(), user);
		synchronized(this.userCache){
			this.userCache.put(element);
		}
	}
	
	public User getUserById(String userId) {
		User user = null;
		synchronized(this.userCache) {
			Element element = this.userCache.get(userId);
			if(element != null) user = (User)element.getObjectValue();
		}
		return user;
	}
	
	public List<User> getAllUsers() {
		userList.clear();
		synchronized(this.userCache) {
			Map<Object, Element> allUserMap = this.userCache.getAll(this.userCache.getKeys());
			Collection<Element> elements = allUserMap.values();
			elements.forEach(e -> userList.add((User)e.getObjectValue()));
		}
		return userList;
	}
	
	public void removeUserById(String userId)
	{
		this.userCache.remove(userId);
	}
	
	public User getUserByPhoneNum(String phoneNum) {
		synchronized(this.userCache) {
			Attribute<String> attr = this.userCache.getSearchAttribute("phoneNum");
			Query query = this.userCache.createQuery().includeValues().addCriteria(attr.eq(phoneNum));
			Results results = query.execute();
			if(results.all().size() > 0){
				List<Result> list = results.all();
				int size = list.size();
				User user = null;
				for(int i = 0 ; i < size; i++){
					user = (User)list.get(i).getValue();
				}
				return user;
			}else return null;
		}
	}
}
