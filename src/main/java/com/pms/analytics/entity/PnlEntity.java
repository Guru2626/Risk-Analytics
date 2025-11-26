package com.pms.analytics.entity;

import java.time.Instant;
import java.util.UUID;

public class PnlEntity {
    private UUID transactionId;
    private UUID portfolioId;
    private String cusipId;
    private String side;
    private Long remainingQuantity;
    private Instant timestamp;
    private float realized_pnl;

}
