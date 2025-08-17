package com.agrilink.rwanda.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.agrilink.rwanda.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProduceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProduceDTO.class);
        ProduceDTO produceDTO1 = new ProduceDTO();
        produceDTO1.setId(1L);
        ProduceDTO produceDTO2 = new ProduceDTO();
        assertThat(produceDTO1).isNotEqualTo(produceDTO2);
        produceDTO2.setId(produceDTO1.getId());
        assertThat(produceDTO1).isEqualTo(produceDTO2);
        produceDTO2.setId(2L);
        assertThat(produceDTO1).isNotEqualTo(produceDTO2);
        produceDTO1.setId(null);
        assertThat(produceDTO1).isNotEqualTo(produceDTO2);
    }
}
