package com.organon.helda.core.gateways;

import com.organon.helda.core.entities.Disassembly;
import com.organon.helda.core.entities.Plan;

public interface DisassemblyGateway {
    Disassembly createDisassembly(String vin);
    Plan startDisassembly(int id, String worker);
}
