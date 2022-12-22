package com.shopme.admin.categories.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.categories.exception.CategoriesNotFoundException;
import com.shopme.admin.categories.repository.CategoriesRepository;
import com.shopme.common.entity.Categories;

@Service
public class CategoriesService {

	public static final int CATEGORY_PER_PAGE = 1;

	@Autowired
	private CategoriesRepository repository;

	public List<Categories> listByPage(CategoriesPageInfo categoriesPageInfo, int pageNum, String sortDir,
			String keyword) {

		Sort sort = Sort.by("name");

		if (sortDir.equals("asc")) {

			sort = sort.ascending();
		} else {
			sort = sort.descending();
		}

		Pageable pageable = PageRequest.of(pageNum - 1, CATEGORY_PER_PAGE, sort);
		Page<Categories> pageCategories = null;

		if (keyword != null && !keyword.isEmpty()) {
			pageCategories = repository.search(keyword, pageable);
		} else {
			pageCategories = repository.findRootCategories(pageable);
		}

		List<Categories> listCategories = pageCategories.getContent();
		categoriesPageInfo.setNoOfElement((int) pageCategories.getTotalElements());
		categoriesPageInfo.setTotalNumberoFPage(pageCategories.getTotalPages());

		if (keyword != null && !keyword.isEmpty()) {

			List<Categories> searchResult = pageCategories.getContent();
			searchResult.forEach(search -> System.out.print(search.getName()));

			return searchResult;
		} else {

			return findHierarchicalCategories(listCategories, sortDir);
		}
	}


	private List<Categories> findHierarchicalCategories(List<Categories> rootCategories, String sortDir) {

		List<Categories> hierarchiCategories = new ArrayList<>();

		for (Categories root : rootCategories) {
			hierarchiCategories.add(Categories.copyAllCategories(root));

			Set<Categories> children = sortedSubCategoriesList(root.getChildren());

			for (Categories subCategories : children) {
				if (root.getParent() == null) {
					String name = "" + subCategories.getName();
					hierarchiCategories.add(Categories.copyAllCategories(subCategories, name));
				}
				listSubHierarchiCategories(hierarchiCategories, subCategories, 1);
			}
		}

		return hierarchiCategories;
	}

	private void listSubHierarchiCategories(List<Categories> hierarchiCategories, Categories parent, int level) {

		Set<Categories> children = sortedSubCategoriesList(parent.getChildren());
		String name = "";
		int newLevel = level + 1;

		for (Categories sub : children) {

			for (int i = 0; i < newLevel; i++)
				name = "-------" + sub.getName();
			hierarchiCategories.add(Categories.copyAllCategories(sub, name));
			listSubCategoriesUsedInForm(hierarchiCategories, sub, 1);
		}
	}

	public Categories save(Categories categories) {

		Categories parent = categories.getParent();
		if (parent != null) {

			String allParentIds = parent.getAllParentIds() == null ? "-" : parent.getAllParentIds();
			allParentIds += String.valueOf(parent.getId())+"-";
			categories.setAllParentIds(allParentIds);
		}
		return repository.save(categories);
	}

	public Categories getById(Integer id) throws CategoriesNotFoundException {

		try {

			return repository.findById(id).get();
		} catch (NoSuchElementException ex) {

			throw new CategoriesNotFoundException("Could not find any user with id:" + id);
		}
	}

	public List<Categories> listCategoriesUsedInForm() {

		/**
		 * not optimze getting bug 😋 fix it sunday
		 */

//		List<Categories> categoriesUsedInForm = new ArrayList<>();

//		Iterable<Categories> categoriesInDB = repository.findAll();

		/*
		 * for (Categories parentCategories : categoriesInDB) {
		 * 
		 * if (parentCategories.getParent() == null) { categoriesUsedInForm.add(new
		 * Categories(parentCategories.getName()));
		 * 
		 * Set<Categories> children = parentCategories.getChildren(); for (Categories
		 * subCat : children) {
		 * 
		 * categoriesUsedInForm.add(new Categories("--" + subCat.getName())); //
		 * System.out.println(subCat.getName()); // printChild(subCat, 1); //
		 * System.out.println(toStringHierarchy(categoriesUsedInForm, subCat, 1));
		 * toStringHierarchy(categoriesUsedInForm, subCat, 1); } } }
		 */

//****************************************************************************************************
//****************************************************************************************************

		List<Categories> categoriesUsedInForm = new ArrayList<>();
		Iterable<Categories> categoriesInDB = repository.findRootCategories(Sort.by("name").ascending());

		for (Categories parent : categoriesInDB) {
			if (parent.getParent() == null)
				categoriesUsedInForm.add(Categories.copyIdAndName(parent));
			// System.out.println(parent.getAlias());

			Set<Categories> child = sortedSubCategoriesList(parent.getChildren());
			for (Categories sub : child) {
				if (parent.getParent() == null) {
					String name = "--" + sub.getName();
					categoriesUsedInForm.add(Categories.copyIdAndName(sub.getId(), name));
					// System.out.println("\t" + sub.getAlias());
				}
				listSubCategoriesUsedInForm(categoriesUsedInForm, sub, 1);
			}
		}
		return categoriesUsedInForm;
	}

	private void listSubCategoriesUsedInForm(List<Categories> categoriesUsedInForm, Categories parent, int level) {

		int newLevel = level + 1;
		Set<Categories> child = sortedSubCategoriesList(parent.getChildren());
		String name = "";

		for (Categories sub : child) {

			for (int i = 0; i < newLevel; i++)
				name = "-------" + sub.getName();
//				name = "\t";
			categoriesUsedInForm.add(Categories.copyIdAndName(sub.getId(), name));

			listSubCategoriesUsedInForm(categoriesUsedInForm, sub, 1);
		}
	}

	private SortedSet<Categories> sortedSubCategoriesList(Set<Categories> children) {

//****************************************************************************************************
		SortedSet<Categories> childCategories = new TreeSet(new Comparator<Categories>() {

			@Override
			public int compare(Categories c1, Categories c2) {
				return c1.getName().compareTo(c2.getName());
			}

		});
		childCategories.addAll(children);
		return childCategories;

//****************************************************************************************************

		// wow awesome 😄 v8
//		SortedSet<Categories> sortedSet = children.stream()
//				.collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Categories::getName))));
//		return sortedSet;

	}

	public void updateStatusEnable(Integer id, boolean enabled) {

		repository.updateEnabledStatus(id, enabled);
	}

	// optimze. but bug please fix it on sunday
	/*
	 * private void listChildren(List<Categories> categoriesUsedInForm, Categories
	 * parent, int subLevel) {
	 * 
	 * int newLevel = subLevel + 1; Set<Categories> children = parent.getChildren();
	 * 
	 * for (Categories subCategories : children) {
	 * 
	 * String name = ""; for (int i = 0; i < newLevel; i++) {
	 * 
	 * name += "--"; } categoriesUsedInForm.add(new Categories(name +
	 * subCategories.getName())); listChildren(categoriesUsedInForm, subCategories,
	 * newLevel); }
	 * 
	 * }
	 */

	/*
	 * private String toStringHierarchy(List<Categories> categoriesUsedInForm,
	 * Categories parent, int tabLevel) { StringBuilder builder = new
	 * StringBuilder(); for (int i = 0; i < tabLevel; i++) { builder.append("\t"); }
	 * builder.append("-" + parent.getName()); builder.append("\n"); for (Categories
	 * nextChild : parent.getChildren()) { // categoriesUsedInForm.add(new
	 * Categories(nextChild.getName()));
	 * builder.append(toStringHierarchy(categoriesUsedInForm, nextChild, tabLevel +
	 * 1)); }
	 * 
	 * String[] builderSplit = builder.toString().split("\n");
	 * 
	 * for (int i = 0; i < builderSplit.length; i++) categoriesUsedInForm.add(new
	 * Categories(builderSplit[i])); return builder.toString(); }
	 */

}
