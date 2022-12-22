package com.shopme.admin.setting.country;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Country;

@RestController
public class CountryRestController {

	@Autowired
	private CountryRepository repository;

	@GetMapping("/countries/list")
	public List<Country> listAll() {

		List<Country> listCountry = repository.findAllByOrderByNameAsc();
		listCountry.forEach(name -> System.out.println(name.getName()));
		return listCountry;
	}

	@PostMapping("/countries/save")
	public String save(Country country) {

		Country saveCountry = repository.save(country);

		return String.valueOf(saveCountry.getId());
	}

	@GetMapping("/countries/delete/{id}")
	public void delete(@PathVariable("id") Integer id) {

		repository.deleteById(id);
	}

}
