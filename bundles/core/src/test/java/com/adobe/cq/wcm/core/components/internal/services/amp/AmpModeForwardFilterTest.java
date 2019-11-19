/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.internal.services.amp;

import static org.mockito.Mockito.mock;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.servlethelpers.MockRequestDispatcherFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
public class AmpModeForwardFilterTest {
    private static final String TEST_BASE = "/amp-mode-forward-filter";
    private static final String TEST_ROOT_PAGE = "/content";
    private static final String AMP_PAGE_PROPERTY = TEST_ROOT_PAGE + "/amp-only";
    private static final String AMP_SELECTOR = TEST_ROOT_PAGE + "/amp-selector";
    private static final String AMP_SELECTOR_WITH_AMP_MODE = TEST_ROOT_PAGE + "/amp-selector-with-amp-mode";

    protected final AemContext context = CoreComponentTestContext.newAemContext();

    private AmpModeForwardFilter ampModeForwardFilter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        ampModeForwardFilter = new AmpModeForwardFilter();
        filterChain = mock(FilterChain.class);
        MockRequestDispatcherFactory mockRequestDispatcherFactory = new MockRequestDispatcherFactory() {
            @Override
            public RequestDispatcher getRequestDispatcher(String s,
                RequestDispatcherOptions requestDispatcherOptions) {
                return mock(RequestDispatcher.class);
            }

            @Override
            public RequestDispatcher getRequestDispatcher(Resource resource,
                RequestDispatcherOptions requestDispatcherOptions) {
                return mock(RequestDispatcher.class);
            }
        };
        context.request().setRequestDispatcherFactory(mockRequestDispatcherFactory);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, TEST_ROOT_PAGE);

    }

    @Test
    void testFilteringWithNoSelector() throws IOException, ServletException {
        context.currentPage(AMP_PAGE_PROPERTY);
        context.currentResource();
        ampModeForwardFilter.doFilter(context.request(), context.response(), filterChain);
    }

    @Test
    void testFilteringWithAmpSelector() throws IOException, ServletException {
        context.currentPage(AMP_SELECTOR);
        context.requestPathInfo().setResourcePath(AMP_SELECTOR);
        //with amp selector
        context.requestPathInfo().setSelectorString("amp");
        context.requestPathInfo().setExtension("html");
        ampModeForwardFilter.doFilter(context.request(), context.response(), filterChain);

        //with .amp selector
        context.requestPathInfo().setSelectorString(".amp");
        context.requestPathInfo().setExtension("html");
        ampModeForwardFilter.doFilter(context.request(), context.response(), filterChain);

        //with amp. selector
        context.requestPathInfo().setSelectorString("amp.");
        context.requestPathInfo().setExtension("html");
        ampModeForwardFilter.doFilter(context.request(), context.response(), filterChain);
    }

    @Test
    void testFilteringWithSelectorWithAmpMode() throws IOException, ServletException {
        context.currentPage(AMP_SELECTOR_WITH_AMP_MODE);
        context.requestPathInfo().setResourcePath(AMP_SELECTOR);
        //with .amp selector
        context.requestPathInfo().setSelectorString(".amp");
        context.requestPathInfo().setExtension("html");
        ampModeForwardFilter.doFilter(context.request(), context.response(), filterChain);
    }

}
