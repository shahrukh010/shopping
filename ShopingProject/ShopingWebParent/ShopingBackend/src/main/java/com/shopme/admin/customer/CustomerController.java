package com.shopme.admin.customer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.exception.CustomerNotFoundException;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@Controller
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@GetMapping("/customers")
	public String listByFirstPage(Model model) {

		return listByPage(model, 1, "firstName", "asc", null);
	}

	@GetMapping("/customers/page/{pageNum}")
	public String listByPage(Model model, @PathVariable(name = "pageNum") Integer pageNum,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword) {

		Page<Customer> pages = customerService.listByPage(pageNum, sortField, sortDir, keyword);
		List<Customer> listCustomer = pages.getContent();

		long startCount = (pageNum - 1) * customerService.CUSTOMER_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		long endCount = startCount + customerService.CUSTOMER_PER_PAGE;

		if (endCount > pages.getTotalElements()) {

			endCount = pages.getTotalElements();
		}

		model.addAttribute("totalPages", pages.getTotalPages());
		model.addAttribute("totalItems", pages.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listCustomers", listCustomer);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("endCount", endCount);

		return "customers/customers";
	}

	@GetMapping("/customers/{id}/enabled/{status}")
	public String updateCustomerEnableStatus(@PathVariable(name = "id") Integer id,
			@PathVariable("status") boolean enable, RedirectAttributes redirectAttributes) {

		customerService.updateCustomerEnableStatus(id, enable);

		String status = enable ? "enabled" : "disabled";
		String message = "The Customer ID:" + id + "has been " + status;
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/customers";

	}

	@GetMapping("/customers/detail/{id}")
	public String viewCustomer(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {

		try {

			Customer customer = customerService.get(id);
			model.addAttribute("customer", customer);

			return "customers/customer_detail_modal";
		} catch (CustomerNotFoundException ex) {

			redirectAttributes.addFlashAttribute("message", ex);

			return "redirect:/customers";
		}

	}

	@GetMapping("/customers/edit/{id}")
	public String editCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {

		try {
			Customer customer = customerService.get(id);
			List<Country> listCountry = customerService.listCountry();
			model.addAttribute("listCountries", listCountry);
			model.addAttribute("customer", customer);
			model.addAttribute("pageTitle", String.format("Edit Customer (ID:%d)", id));

			return "customers/customer_form";
		} catch (CustomerNotFoundException ex) {

			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/customers";
		}
	}

	@PostMapping("/customers/save")
	public String saveCustomer(Customer customer, Model model, RedirectAttributes redirectAttribute) {

		customerService.save(customer);
		redirectAttribute.addFlashAttribute("message", "The Customer ID " + customer.getId() + " has been updated ");
		return "redirect:/customers";
	}

	@GetMapping("/customers/delete/{id}")
	public String customerDelete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {

		try {
			customerService.delete(id);
			redirectAttributes.addFlashAttribute("message",
					"The Customer ID " + id + "+ has been deleted successfully");

		} catch (CustomerNotFoundException ex) {

			redirectAttributes.addFlashAttribute("message", ex);
		}
		return "redirect:/customers";

	}

}
