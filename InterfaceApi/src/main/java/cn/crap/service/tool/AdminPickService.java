package cn.crap.service.tool;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.crap.dto.LoginInfoDto;
import cn.crap.dto.PickDto;
import cn.crap.enumeration.ArticleType;
import cn.crap.enumeration.DataType;
import cn.crap.enumeration.FontFamilyType;
import cn.crap.enumeration.MenuType;
import cn.crap.enumeration.ProjectStatus;
import cn.crap.enumeration.SettingType;
import cn.crap.enumeration.UserType;
import cn.crap.framework.MyException;
import cn.crap.inter.service.table.IArticleService;
import cn.crap.inter.service.table.IMenuService;
import cn.crap.inter.service.table.IProjectService;
import cn.crap.inter.service.table.IRoleService;
import cn.crap.inter.service.tool.IPickService;
import cn.crap.model.Article;
import cn.crap.model.Menu;
import cn.crap.model.Project;
import cn.crap.model.Role;
import cn.crap.utils.Const;
import cn.crap.utils.MyString;
import cn.crap.utils.Tools;

/**
 * 下拉选着
 * @author Ehsan
 *
 */
@Service("adminPickService")
public class AdminPickService implements IPickService{
	@Autowired
	IMenuService menuService;
	@Autowired
	private IProjectService projectService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IArticleService articleService;

	
	public enum op{ 
		CATEGORY(1,"CATEGORY"),
		PROJECT(2,"PROJECT"),
		PROJECTSTATUE(3,"PROJECTSTATUE"),
		MENU(4,"MENU"),
		AUTH(5,"AUTH"),
		ROLE(6,"ROLE"),
		WEBPAGETYPE(7,"WEBPAGETYPE"),
		MENUTYPE(8,"MENUTYPE"),
		SETTINGTYPE(9,"SETTINGTYPE"),
		DATATYPE(10,"DATATYPE"),
		MODELNAME(11,"MODELNAME"),
		MENURUL(12,"MENURUL"),
		FONTFAMILY(13,"FONTFAMILY"),
		USERTYPE(14,"USERTYPE");
		
		private int value;
		private String name;
		 op(int value,String name){
			this.name=name;
			this.value=value;
		}
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	//@Override
	public void getPickList(List<PickDto> picks, String code, String key, LoginInfoDto user) throws MyException {
		PickDto pick = null;
		String preUrl = "";
		/*op c=cn.crap.utils.EnumUtils.valueOf(op.class,code);
		switch (c) {
		case CATEGORY:*/
		int i = 0;
		if("CATEGORY".equals(code)){
			@SuppressWarnings("unchecked")
			List<String> categorys = (List<String>) articleService.queryByHql("select distinct category from Article where moduleId='web' "
					+ "or moduleId in( select id from Module where userId='" + user.getId()+"')", null);
			for (String w : categorys) {
				if (w == null)
					continue;
				i++;
				pick = new PickDto("cat_" + i, w, w);
				picks.add(pick);
			}
			return;
		}
			
//		case "MYPROJECT":
//			for (Project p : projectService.findByMap(null, null, null)) {
//				pick = new PickDto(p.getId(), p.getName());
//				picks.add(pick);
//			}
//			return;
		else if("PROJECT".equals(code)){
			for (Project p : projectService.findByMap(null, null, null)) {
				pick = new PickDto(p.getId(), p.getName());
				picks.add(pick);
			}
			return;
		}else if("PROJECTSTATUE".equals(code)){// 管理员项目、推荐项目...
			for (ProjectStatus ps : ProjectStatus.values()) {
				pick = new PickDto(ps.name(), ps.getStatus()+"", ps.getName());
				picks.add(pick);
			}
			return;
		}else if("MENU".equals(code)){// 一级菜单
			for (Menu m : menuService.findByMap(Tools.getMap("parentId", "0"), null, null)) {
				pick = new PickDto(m.getId(), m.getMenuName());
				picks.add(pick);
			}
			return;
		}else if("AUTH".equals(code)){// 权限
			// 分割线
			pick = new PickDto(Const.SEPARATOR, "用户、菜单、角色、系统设置管理");
			picks.add(pick);
			pick = new PickDto(DataType.USER.name(), "用户管理");
			picks.add(pick);
			pick = new PickDto(DataType.ROLE.name(), "角色管理");
			picks.add(pick);
			pick = new PickDto(DataType.MENU.name(), "菜单管理");
			picks.add(pick);
			pick = new PickDto(DataType.SETTING.name(), "系统设置管理");
			picks.add(pick);
			pick = new PickDto(ArticleType.ARTICLE.name(), "站点文章管理");
			picks.add(pick);
			pick = new PickDto(ArticleType.PAGE.name(), "站点页面管理");
			picks.add(pick);
			pick = new PickDto(DataType.LOG.name(), "操作日志管理");
			picks.add(pick);
			
			return;
		}else if("ROLE".equals(code)){ // 角色
			pick = new PickDto(Const.SUPER, "超级管理员");
			picks.add(pick);
			for (Role r : roleService.findByMap(null, null, null)) {
				pick = new PickDto(r.getId(), r.getRoleName());
				picks.add(pick);
			}
			return;
		}else if("WEBPAGETYPE".equals(code)){// 枚举 webPage
			for (ArticleType type : ArticleType.values()) {
				pick = new PickDto(type.name(), type.getName());
				picks.add(pick);
			}
			return;
		}else if("MENUTYPE".equals(code)){// 枚举 菜单类型
			for (MenuType type : MenuType.values()) {
				pick = new PickDto(type.name(), type.getName());
				picks.add(pick);
			}
			return;
		}else if("SETTINGTYPE".equals(code)){// 枚举 设置类型
			for (SettingType type : SettingType.values()) {
				pick = new PickDto(type.name(), type.getName());
				picks.add(pick);
			}
			return;
		}else if("DATATYPE".equals(code)){// 枚举 数据类型
			for (DataType status : DataType.values()) {
				pick = new PickDto(status.name(), status.getName());
				picks.add(pick);
			}
			return;
		}else if("MODELNAME".equals(code)){// 数据类型
			i = 0;
			List<String> modelNames = (List<String>) articleService.queryByHql("select distinct modelName from Log", null);
			for (String w : modelNames) {
				if (w == null)
					continue;
				i++;
				pick = new PickDto("modelName_" + i, w, w);
				picks.add(pick);
			}
			return;
		}else if("MENURUL".equals(code)){
				// 分割线
				pick = new PickDto(Const.SEPARATOR, "项目列表");
				picks.add(pick);
				pick = new PickDto("m_myproject", "#/project/list/true/NULL", "我的项目列表");
				picks.add(pick);
				pick = new PickDto("m_myproject", "#/project/list/false/NULL", "推荐项目列表");
				picks.add(pick);
				
				pick = new PickDto(Const.SEPARATOR, "项目主页【推荐项目】");
				picks.add(pick);
				
				for (Project project : projectService.findByMap(Tools.getMap("status", ProjectStatus.RECOMMEND.getStatus()), null, null)) {
					pick = new PickDto(project.getId() , String.format(Const.FRONT_PROJECT_URL, project.getId()) , project.getName());
					picks.add(pick);
				}
				
				// 分割线
				pick = new PickDto(Const.SEPARATOR, "文章列表【站点文章】");
				picks.add(pick);
				int j = 0;
				@SuppressWarnings("unchecked")
				List<String> categorys2 = (List<String>) articleService.queryByHql("select distinct category from Article where moduleId='web'", null);
				for (String article : categorys2) {
					if (MyString.isEmpty(article))
						continue;
					j++;
					pick = new PickDto("cat_" + j, String.format(Const.FRONT_ARTICLE_URL, Const.WEB_MODULE,  Const.WEB_MODULE, article) , article);
					picks.add(pick);
				}
				
				// 分割线
				pick = new PickDto(Const.SEPARATOR, "页面【站点页面】");
				picks.add(pick);
				preUrl = "#/web/article/detail/web/PAGE/";
				for (Article w : articleService
						.findByMap(Tools.getMap("key|" + Const.NOT_NULL, Const.NOT_NULL, "type", "PAGE"), null, null)) {
					pick = new PickDto("wp_" + w.getKey(), preUrl + w.getKey(), w.getName());
					picks.add(pick);
				}
				// 分割线
				return;
		}else if("FONTFAMILY".equals(code)){// 字体
			for (FontFamilyType font : FontFamilyType.values()) {
				pick = new PickDto(font.name(), font.getValue(), font.getName());
				picks.add(pick);
			}
			return;
		}else if("USERTYPE".equals(code)){ // 用户类型
			for (UserType type : UserType.values()) {
				pick = new PickDto("user-type"+type.getType(), type.getType()+"", type.getName());
				picks.add(pick);
			}
			return;
		}
	}

}
