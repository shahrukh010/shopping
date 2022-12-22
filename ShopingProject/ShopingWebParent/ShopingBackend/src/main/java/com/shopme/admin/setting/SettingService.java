package com.shopme.admin.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

@Service
public class SettingService {

	@Autowired
	private SettingRepository settingRepository;

	public List<Setting> listAllSetting() {

		return (List<Setting>) settingRepository.findAll();
	}

	public GeneralSettingBag generalSettingBag() {

		List<Setting> settings = new ArrayList<>();

		List<Setting> generalSetting = settingRepository.findByCategory(SettingCategory.GENERAL);
		List<Setting> currencySetting = settingRepository.findByCategory(SettingCategory.CURRENCY);

		settings.addAll(generalSetting);
		settings.addAll(currencySetting);

		return new GeneralSettingBag(settings);
	}

	public void saveAll(Iterable<Setting> setting) {

		settingRepository.saveAll(setting);
	}

	public List<Setting> getMailServerSetting() {

		return settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
	}

	public List<Setting> getMailTemplateSetting() {

		return settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATE);
	}

}
