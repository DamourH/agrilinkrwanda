package com.agrilink.rwanda.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.agrilink.rwanda.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FarmerProduceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FarmerProduceDTO.class);
        FarmerProduceDTO farmerProduceDTO1 = new FarmerProduceDTO();
        farmerProduceDTO1.setId(1L);
        FarmerProduceDTO farmerProduceDTO2 = new FarmerProduceDTO();
        assertThat(farmerProduceDTO1).isNotEqualTo(farmerProduceDTO2);
        farmerProduceDTO2.setId(farmerProduceDTO1.getId());
        assertThat(farmerProduceDTO1).isEqualTo(farmerProduceDTO2);
        farmerProduceDTO2.setId(2L);
        assertThat(farmerProduceDTO1).isNotEqualTo(farmerProduceDTO2);
        farmerProduceDTO1.setId(null);
        assertThat(farmerProduceDTO1).isNotEqualTo(farmerProduceDTO2);
    }
}
