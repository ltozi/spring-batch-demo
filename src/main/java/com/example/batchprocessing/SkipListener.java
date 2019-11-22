package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.SkipListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class SkipListener extends SkipListenerSupport<Person, Person> {

	private static final Logger log = LoggerFactory.getLogger(SkipListener.class);


	@Override
	public void onSkipInRead(Throwable t) {
		log.warn("Skipping because ", t.getCause());
		super.onSkipInRead(t);
	}
}