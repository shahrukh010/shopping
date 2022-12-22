package com.shopme;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		exposeUserDirectory("user-photo", registry);
		exposeDirectory("../category-images", registry);
		exposeDirectory("../product-images", registry);
		exposeDirectory("../site-logo", registry);
	}

	public void exposeUserDirectory(String pathPattern, ResourceHandlerRegistry registry) {

		Path path = Paths.get(pathPattern);
		String absolutePath = path.toFile().getAbsolutePath();
//		String logicPath = pathPattern.replace("","") + "/**";

		registry.addResourceHandler("/" + pathPattern + "/**").addResourceLocations("file://" + absolutePath + "/");

	}

	public void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry) {
		Path path = Paths.get(pathPattern);
		String absolutePath = path.toFile().getAbsolutePath();
		String logicPath = pathPattern.replace("../", "") + "/**";

		registry.addResourceHandler(logicPath).addResourceLocations("file://" + absolutePath + "/");

//****************************************************************************************************
//		String dirName = "user-photo";
//		Path userPathDir = Paths.get(dirName);
//
//		String userPhotoPath = userPathDir.toFile().getAbsolutePath();
//
//		registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file://" + userPhotoPath + "/");
//
////****************************************************************************************************
//
//		String categoriesImageDirName = "../category-images";
//		Path categoriesPathDir = Paths.get(categoriesImageDirName);
//		String categoriesImagesPath = categoriesPathDir.toFile().getAbsolutePath();
//		registry.addResourceHandler("/category-images" + "/**")
//				.addResourceLocations("file://" + categoriesImagesPath + "/");
//
////****************************************************************************************************
//
//		String brandImageDirName = "../brand-images";
//		Path brandPathDir = Paths.get(brandImageDirName);
//		String brandImagePath = brandPathDir.toFile().getAbsolutePath();
//		registry.addResourceHandler("/brand-images/**").addResourceLocations("file://" + brandImagePath);
//
////****************************************************************************************************

	}

}
