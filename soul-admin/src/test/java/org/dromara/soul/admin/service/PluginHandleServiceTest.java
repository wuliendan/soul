/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.soul.admin.service;

import org.dromara.soul.admin.dto.PluginHandleDTO;
import org.dromara.soul.admin.entity.PluginHandleDO;
import org.dromara.soul.admin.entity.SoulDictDO;
import org.dromara.soul.admin.mapper.PluginHandleMapper;
import org.dromara.soul.admin.mapper.SoulDictMapper;
import org.dromara.soul.admin.page.CommonPager;
import org.dromara.soul.admin.page.PageParameter;
import org.dromara.soul.admin.query.PluginHandleQuery;
import org.dromara.soul.admin.service.impl.PluginHandleServiceImpl;
import org.dromara.soul.admin.vo.PluginHandleVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.Silent.class)
public final class PluginHandleServiceTest {

    @InjectMocks
    private PluginHandleServiceImpl pluginHandleService;

    @Mock
    private PluginHandleMapper pluginHandleMapper;

    @Mock
    private SoulDictMapper soulDictMapper;

    @Before
    public void setUp() {
        pluginHandleService = new PluginHandleServiceImpl(pluginHandleMapper, soulDictMapper);
    }

    @Test
    public void testListByPage() {
        final List<PluginHandleDO> pluginHandleDOs = buildPluginHandleDOList();
        given(this.pluginHandleMapper.countByQuery(any())).willReturn(1);
        given(this.pluginHandleMapper.selectByQuery(any())).willReturn(pluginHandleDOs);
        PluginHandleQuery params = buildPluginHandleQuery();
        final CommonPager<PluginHandleVO> result = this.pluginHandleService.listByPage(params);
        assertThat(result, notNullValue());
        assertEquals(pluginHandleDOs.size(), result.getDataList().size());
    }

    private List<PluginHandleDO> buildPluginHandleDOList() {
        PluginHandleDO pluginHandleDO = buildPluginHandleDO();
        return Collections.singletonList(pluginHandleDO);
    }

    private PluginHandleDO buildPluginHandleDO() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        return PluginHandleDO.builder()
                .pluginId("4")
                .field("burstCapacity")
                .label("capacity")
                .dataType(2)
                .type(2)
                .sort(1)
                .dateCreated(now)
                .dateUpdated(now)
                .build();
    }

    private PluginHandleQuery buildPluginHandleQuery() {
        return PluginHandleQuery.builder()
                .pluginId("4")
                .pageParameter(buildPageParameter())
                .build();
    }

    private PageParameter buildPageParameter() {
        final PageParameter result = new PageParameter();
        result.setCurrentPage(1);
        result.setPageSize(12);
        result.setOffset(0);
        return result;
    }

    @Test
    public void testCreatePluginHandle() {
        final int insertCount = 1;
        given(this.pluginHandleMapper.insertSelective(any())).willReturn(insertCount);
        final PluginHandleDTO params = buildPluginHandleDTO();
        final Integer result = this.pluginHandleService.createOrUpdate(params);
        assertThat(result, equalTo(insertCount));
    }

    private PluginHandleDTO buildPluginHandleDTO() {
        final PluginHandleDTO result = new PluginHandleDTO();
        result.setPluginId("4");
        result.setField("test");
        result.setLabel("test");
        result.setDataType(2);
        result.setType(2);
        result.setSort(3);
        return result;
    }

    @Test
    public void testUpdatePluginHandle() {
        final int updateCount = 1;
        given(this.pluginHandleMapper.updateByPrimaryKeySelective(any())).willReturn(updateCount);
        final PluginHandleDTO params = buildPluginHandleDTO();
        params.setId("1211");
        final Integer result = this.pluginHandleService.createOrUpdate(params);
        assertThat(result, equalTo(updateCount));
    }

    @Test
    public void testDeletePluginHandles() {
        final List<String> ids = Collections.singletonList("1211");
        given(this.pluginHandleMapper.delete(any())).willReturn(1);
        final Integer result = this.pluginHandleService.deletePluginHandles(ids);
        assertThat(result, equalTo(1));
    }

    @Test
    public void testFindById() {
        PluginHandleDO pluginHandleDO = buildPluginHandleDO();
        given(this.pluginHandleMapper.selectById("4")).willReturn(pluginHandleDO);
        final PluginHandleVO result = this.pluginHandleService.findById("4");
        assertThat(result, notNullValue());
        assertEquals(pluginHandleDO.getPluginId(), result.getPluginId());
    }

    @Test
    public void testFindByIdWhenDataTypeEqualSelectBox() {
        PluginHandleDO pluginHandleDO = buildPluginHandleDO();
        pluginHandleDO.setDataType(3);
        given(this.pluginHandleMapper.selectById("4")).willReturn(pluginHandleDO);
        given(this.soulDictMapper.findByType(any())).willReturn(buildSoulDictDOs());
        final PluginHandleVO result = this.pluginHandleService.findById("4");
        assertThat(result, notNullValue());
        assertThat(result.getDictOptions().size(), equalTo(1));
    }

    private List<SoulDictDO> buildSoulDictDOs() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        final SoulDictDO result = SoulDictDO.builder()
                .type("burstCapacity")
                .dictCode("RATE_LIMITER_QPS")
                .dictName("QPS")
                .dictValue("1")
                .desc("rate limiter qps")
                .enabled(true)
                .sort(0)
                .dateCreated(now)
                .dateUpdated(now)
                .build();
        return Collections.singletonList(result);
    }

    @Test
    public void testList() {
        final List<PluginHandleDO> pluginHandleDOs = buildPluginHandleDOList();
        given(this.pluginHandleMapper.selectByQuery(any())).willReturn(pluginHandleDOs);
        final List<PluginHandleVO> result = pluginHandleService.list("4", 2);
        assertThat(result, notNullValue());
        assertEquals(pluginHandleDOs.size(), result.size());
    }
}
