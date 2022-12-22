package com.shopme.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Categories;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	public List<Categories> listNoChildrenCategory() {

		List<Categories> listNoChildrenCategories = new ArrayList<>();
		List<Categories> listEnabledCategories = repository.findEnabled();

		listEnabledCategories.forEach(category -> {

			Set<Categories> children = category.getChildren();

			if (children == null || children.size() == 0)
				listNoChildrenCategories.add(category);
		});
		return listNoChildrenCategories;
	}

	public Categories getCategory(String alias) {

		return repository.FindByAlias(alias);
	}

	public List<Categories> getCategoryParent(Categories child) {

		List<Categories> listParent = new ArrayList<>();

		Categories parent = child.getParent();
		while (parent != null) {

			listParent.add(0, parent);
			parent = parent.getParent();
		}
		listParent.add(child);
		return listParent;
	}

}
