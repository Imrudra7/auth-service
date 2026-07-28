package in.maithilart.auth.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import in.maithilart.common.context.provider.MicroserviceNameProvider;

@Component
public class AuthMicroserviceNameProvider implements MicroserviceNameProvider {

	@Value("${spring.application.name}")
	private String applicationName;

	@Override
	public String getMicroservicename() {
		// TODO Auto-generated method stub
		return applicationName;
	}

}
