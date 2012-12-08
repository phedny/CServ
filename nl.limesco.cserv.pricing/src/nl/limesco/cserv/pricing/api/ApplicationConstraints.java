package nl.limesco.cserv.pricing.api;

import java.util.Calendar;
import java.util.Collection;

import com.google.common.base.Optional;

public interface ApplicationConstraints {

	Calendar getValidFrom();

	Optional<Calendar> getValidUntil();

	Optional<Collection<String>> getSources();

}