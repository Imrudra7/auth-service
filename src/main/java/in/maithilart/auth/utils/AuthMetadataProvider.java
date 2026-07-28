package in.maithilart.auth.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import in.maithilart.common.context.provider.MetadataProvider;

@Component
public class AuthMetadataProvider implements MetadataProvider {

	@Override
	public Map<String, Object> getMetadata() {
		Map<String, Object> metadata = new HashMap<>();

		metadata.put("correlationId", MDC.get("pulse"));

		return metadata;
	}

}
