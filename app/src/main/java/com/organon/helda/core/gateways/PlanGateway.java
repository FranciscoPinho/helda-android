package com.organon.helda.core.gateways;

import com.organon.helda.core.entities.Plan;

import java.io.InputStream;
import java.util.Locale;

public interface PlanGateway {
    Plan  getPlan(String model, String locale);
}
