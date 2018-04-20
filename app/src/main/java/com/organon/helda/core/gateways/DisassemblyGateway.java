package com.organon.helda.core.gateways;

import com.organon.helda.core.entities.Disassembly;

public interface DisassemblyGateway {
    Disassembly startDisassembly(String worker);
}
