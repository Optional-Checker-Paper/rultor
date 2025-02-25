/**
 * Copyright (c) 2009-2023 Yegor Bugayenko
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the rultor.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.rultor.agents.github.qtn;

import com.jcabi.aspects.Immutable;
import com.jcabi.github.Comment;
import com.jcabi.http.Request;
import com.jcabi.http.Response;
import com.jcabi.log.Logger;
import com.rultor.agents.github.Question;
import com.rultor.agents.github.Req;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mark this question in GitHub with an emoji, to show the
 * author that the comments has been seen.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.50.0
 */
@Immutable
@ToString
@EqualsAndHashCode(of = "origin")
public final class QnReaction implements Question {

    /**
     * Original question.
     */
    private final transient Question origin;

    /**
     * Ctor.
     * @param qtn Original question
     */
    public QnReaction(final Question qtn) {
        this.origin = qtn;
    }

    @Override
    public Req understand(final Comment.Smart comment,
        final URI home) throws IOException {
        final String emoji = "heart";
        final Response res = comment.issue().repo().github().entry().uri()
            .path("/repos")
            .path(comment.issue().repo().coordinates().user())
            .path(comment.issue().repo().coordinates().repo())
            .path("issues/comments")
            .path(Integer.toString(comment.number()))
            .path("reactions")
            .back()
            .method(Request.POST)
            .body()
            .set(String.format("{\"content\": \"%s\"}", emoji))
            .back()
            .fetch();
        if (res.status() == HttpURLConnection.HTTP_CREATED) {
            Logger.info(
                this, "Emoji '%s' to GitHub comment #%d in %s",
                emoji,
                comment.number(),
                comment.issue().repo().coordinates()
            );
        }
        return this.origin.understand(comment, home);
    }

}
