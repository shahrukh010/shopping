package com.shopme.common.entity;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "categories")
public class Categories {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(length = 150, nullable = false, unique = true)
	private String name;
	@Column(length = 150, nullable = false, unique = true)
	private String alias;
	@Column(length = 150, nullable = false)
	private String images;
	private boolean enabled;

	@Column(name = "all_parent_id", length = 256, nullable = true)
	private String allParentIds;

	@OneToOne
	@JoinColumn(name = "parent_id")
	private Categories parent;

	public String getAllParentIds() {
		return allParentIds;
	}

	public void setAllParentIds(String allParentIds) {
		this.allParentIds = allParentIds;
	}

	@OneToMany(mappedBy = "parent")
	@OrderBy("name asc")
	private Set<Categories> children = new HashSet<>();

	public Categories() {
	}


	public Categories(String name, Categories parent) {

		this(name);
		this.parent = parent;
	}

	public Categories(String name) {
		this.name = name;
		this.alias = name;
		this.images = "default.png";

	}
	
	public Categories(int id) {
		this.id = id;
	}

	public Categories(Integer id) {

		this.id = id;
	}

	public static Categories copyIdAndName(Categories categories) {

		Categories copyCategories = new Categories();
		copyCategories.setId(categories.getId());
		copyCategories.setName(categories.getName());

		return copyCategories;
	}

	public static Categories copyIdAndName(int id, String name) {

		Categories copyCategories = new Categories();
		copyCategories.setId(id);
		copyCategories.setName(name);

		return copyCategories;
	}

	// for dropdown list
	public static Categories copyAllCategories(Categories categories) {

		Categories copyCategories = new Categories();
		copyCategories.setId(categories.getId());
		copyCategories.setName(categories.getName());
		copyCategories.setAlias(categories.getAlias());
		copyCategories.setImages(categories.getImages());
		copyCategories.setEnabled(categories.isEnabled());
		return copyCategories;
	}

	// for main window required only categories name.
	public static Categories copyAllCategories(Categories categories, String name) {

		Categories copy = Categories.copyAllCategories(categories);
		System.out.println("debug" + copy);
		copy.setName(name);
		return copy;
	}

	public void setParent(Categories parent) {
		this.parent = parent;
	}

	public void setChildren(Set<Categories> children) {
		this.children = children;
	}

	public Categories getParent() {
		return parent;
	}

	public Set<Categories> getChildren() {
		return children;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}
	
	


	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Transient
	public String getImagePath() {

		if (id == null) {
			return "/images/default-user.png";
		}

		return "/category-images/" + this.id + "/" + this.images;
	}

	@Override
	public String toString() {

		return this.name;
	}

	// do not use toString,you will get stackoverflow. you spend 1hr to debug please
	// take care.

//	@Override
//	public String toString() {
//		return "Categories [id=" + id + ", name=" + name + ", alias=" + alias + ", images=" + images + ", enabled="
//				+ enabled + ", parent=" + parent + ", child=" + children + "]";
//	}

}
