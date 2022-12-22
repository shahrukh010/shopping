package com.shopme.admin.setting;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Currency;
import com.shopme.common.entity.Setting;

@Controller
public class SettingController {

	@Autowired
	private SettingService settingService;
	@Autowired
	private CurrencyRepository currencyRepo;

	@GetMapping("/settings")
	public String listAll(Model model) {

		List<Setting> listSettings = settingService.listAllSetting();
		List<Currency> listCurrencies = currencyRepo.findAllByOrderByNameAsc();
		model.addAttribute("listCurrencies", listCurrencies);

		for (Setting setting : listSettings) {

			System.out.println(setting.getValue() + ":" + setting.getValue());

			model.addAttribute(setting.getKey(), setting.getValue());
		}

		return "settings/settings";

	}

	@PostMapping("/settings/save_general")
	public String saveGeneralSetting(@RequestParam("fileImage") MultipartFile multipartFile,
			HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) throws IOException {

		GeneralSettingBag settingBag = settingService.generalSettingBag();

		saveSiteLogo(multipartFile, settingBag);
		saveCurrencySymbol(httpServletRequest, settingBag);
		updateSettingValueFrom(httpServletRequest, settingBag.list());

		redirectAttributes.addFlashAttribute("message", "General setting have been save");
		return "redirect:/settings";
	}

	private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {

		if (!multipartFile.isEmpty()) {

			String fileName = org.springframework.util.StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String value = "/site-logo/" + fileName;
			settingBag.updateSiteLogo(value);
			String uploadDir = "../site-logo/";
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}

	}

	private void saveCurrencySymbol(HttpServletRequest httpServletRequest, GeneralSettingBag settingBag) {

		Integer currencyId = Integer.parseInt(httpServletRequest.getParameter("CURRENCY_ID"));
		Optional<Currency> findByResult = currencyRepo.findById(currencyId);

		if (findByResult.isPresent()) {

			Currency currency = findByResult.get();
			settingBag.updateCurrencySymbol(currency.getSymbol());
		}
	}

	private void updateSettingValueFrom(HttpServletRequest httpServletRequest, List<Setting> listSetting) {

		for (Setting setting : listSetting) {

			String value = httpServletRequest.getParameter(setting.getKey());

			if (value != null) {

				setting.setValue(value);
			}
		}
		settingService.saveAll(listSetting);
	}

	@PostMapping("/settings/save_mail_server")
	public String getMailServerSetting(HttpServletRequest httpServletReques, RedirectAttributes redirectAttributes) {

		List<Setting> mailServerSetting = settingService.getMailServerSetting();

		updateSettingValueFrom(httpServletReques, mailServerSetting);
		redirectAttributes.addFlashAttribute("message", "Mail server setting have been save");

		return "redirect:/settings";
	}

	@PostMapping("/settings/save_mail_templates")
	public String getMailTemplateSetting(HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {

		List<Setting> mailTemplateSetting = settingService.getMailTemplateSetting();

		updateSettingValueFrom(httpServletRequest, mailTemplateSetting);
		redirectAttributes.addFlashAttribute("message", "Mail template setting have been save");

		return "redirect:/settings";
	}

}
