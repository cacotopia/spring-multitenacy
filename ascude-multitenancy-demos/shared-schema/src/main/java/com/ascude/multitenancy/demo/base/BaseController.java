
package com.ascude.multitenancy.demo.base;
import com.ascude.multitenancy.demo.service.BlogArticleService;
import com.ascude.multitenancy.demo.service.BlogChannelService;
import com.ascude.multitenancy.demo.service.BlogCommentService;
import com.ascude.multitenancy.demo.service.BlogTagsService;

public class BaseController {

	protected BlogArticleService blogArticleService;

	protected BlogChannelService blogChannelService;

	protected BlogCommentService blogCommentService;

	protected BlogTagsService blogTagsService;

}
