/**
 * @file
 * Menu custo behaviors
 */

(function ($, Drupal) {
  Drupal.behaviors.rhd_menuSearch = {
    attach: function (context, settings) {
      $('[data-rhd-nav-search-toggle]').once('menuSearch').each(function() {
        $(this).on('click touch', function(e) {
          e.preventDefault();
          var $input = $(this).siblings('.rhd-nav-search').addClass('shown').find('[data-rhd-nav-search-input]');
          // Need a set timeout b/c otherwise .focus() will cause scrolling inside the container
          window.setTimeout(function() {
            $input.focus()
          }, 220);
        })
      });
      $('[data-rhd-nav-search-close]').once('menuSearch').each(function() {
        $(this).on('click touch', function(e) {
          e.preventDefault();
          $(this).parents('.rhd-nav-search').removeClass('shown');
        })
      });
    }
  };

  Drupal.behaviors.rhd_topicsMobileScroll = {
    attach: function(context, settings) {
      $('.rhd-l-topics').once('topicsMobileScroll').each(function() {
        var $this = $(this);
        var $left = $this.find('[data-topics-scroll-left]');
        var $right = $this.find('[data-topics-scroll-right]');
        var content = $this.find('.rhd-c-nav--topics-wrapper')[0];
        var updateScrollButtons = function() {
          if (content.clientWidth >= content.scrollWidth) {
            $left.hide(); $right.hide();
            return;
          }
          $left.toggle(content.scrollLeft !== 0);
          $right.toggle(content.scrollLeft < content.scrollWidth - content.clientWidth);
        };
        var step = 100;
        $left.on('click touch', function() {
          $(content).animate({scrollLeft: content.scrollLeft - 100 }, {complete: updateScrollButtons});
        });
        $right.on('click touch', function() {
          $(content).animate({scrollLeft: content.scrollLeft + 100 }, {complete: updateScrollButtons});
        });
        updateScrollButtons();
        $(window).resize(updateScrollButtons);
        $(content).scroll(updateScrollButtons);
      });
    }
  }
})(jQuery, Drupal);
;
/**
* See the following change record for more information,
* https://www.drupal.org/node/2815083
* @preserve
**/

(function (Drupal) {
  Drupal.behaviors.rhd_ActiveLinks = {
    attach: function attach(context, settings) {
      var path = settings.path;
      var queryString = JSON.stringify(path.currentQuery);
      var querySelector = path.currentQuery ? '[data-drupal-link-query=\'' + queryString + '\']' : ':not([data-drupal-link-query])';
      var originalSelectors = ['[data-drupal-link-system-path="' + path.currentPath + '"]'];
      var selectors = void 0;

      if (path.isFront) {
        originalSelectors.push('[data-drupal-link-system-path="<front>"]');
      }

      selectors = [].concat(originalSelectors.map(function (selector) {
        return selector + ':not([hreflang])';
      }), originalSelectors.map(function (selector) {
        return selector + '[hreflang="' + path.currentLanguage + '"]';
      }));

      selectors = selectors.map(function (current) {
        return current + querySelector;
      });

      var activeLinks = context.querySelectorAll(selectors.join(','));
      var il = activeLinks.length;
      for (var i = 0; i < il; i++) {
        activeLinks[i].classList.add('pf-m-current');
      }
    },
    detach: function detach(context, settings, trigger) {
      if (trigger === 'unload') {
        var activeLinks = context.querySelectorAll('[data-drupal-link-system-path].pf-m-current');
        var il = activeLinks.length;
        for (var i = 0; i < il; i++) {
          activeLinks[i].classList.remove('pf-m-current');
        }
      }
    }
  };
})(Drupal);
;
/**
 * @file
 * Opens in a new tab all (except Download Manager) links ending in .pdf extension
 */

(function ($, Drupal) {
  Drupal.behaviors.rhd_pdfLinks = {
    attach: function (context, settings) {
      $("a[href$='.pdf']", context).once('rhdHandlePDFs').each(function() {

        if (this.href.indexOf('/download-manager/') != -1) {
          return;
        }

        if (this.href.indexOf(location.hostname) == -1) {
          $(this).append(" <i class='far fa-file-pdf'></i>");
          $(this).attr({ target: "_blank" });
        }
      });
    }
  }
})(jQuery, Drupal);
;
/**
 * @file
 * Articles custom behaviors
 */

(function ($, Drupal) {
  Drupal.behaviors.rhd_articleToc = {
    attach: function (context, settings) {
      $('.toc.gsi-nav', context).once('rhd-toc-gsi').each(function () {
        var html = '';
        var articleHeadings = $('.gsi.fetch-toc h2');

        articleHeadings.each(function() {
          var replace_id = $(this).text().replace(/[^a-zA-Z0-9_]/g, '_').toLowerCase();
          $(this).attr('id', replace_id);
          html += "<li><a href='#" + replace_id + "'>" + $(this).text() + "</a></li>";
        });

        $(this).html(html);
      });
    }
  }
})(jQuery, Drupal);
;
