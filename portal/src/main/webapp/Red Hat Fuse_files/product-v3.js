( function( window, $, rh, undefined ) {
    Drupal.behaviors.webux = {
        attach: function( context, settings ) {
            rh.webux = {};

            // jscs:disable validateIndentation

// Requires the utils.js file be included as well for breakpoint validation
if ( typeof rh.webux.utils === "undefined" ) {
    //--- Global utility variables
rh.webux.utils = {
    lg: function() {
        return $( window ).width() >= 1200;
    },
    md: function() {
        return $( window ).width() >= 992 && $( window ).width() < 1200;
    },
    sm: function() {
        return $( window ).width() >= 768 && $( window ).width() < 992;
    },
    xs: function() {
        return $( window ).width() >= 480 && $( window ).width() < 768;
    },
    xxs: function() {
        return $( window ).width() < 480;
    },
    isDesktop: $( window ).width >= 768,
    isTablet: $( window ).width >= 480 && $( window ).width < 768,
    isMobile: $( window ).width < 480,
    breakpoints: [ "xxs", "xs", "sm", "md", "lg" ],
    url: {
        root: window.location.hostname,
        path: window.location.pathname.split( "/" ).splice( 1 )
    },
    lang:   window.location.pathname.split( "/" ).splice( 1 )[ 0 ]
};

}

rh.webux.toggle = {
    attr: {
        openEvent: "data-ux-toggle-open",
        closeEvent: "data-ux-toggle-close-on",
        toggleID: "data-ux-toggle-id",
        toggleTarget: "data-ux-toggle-target",
        state: "data-ux-state",
        align: "data-ux-align-element",
        toggleSingle: "data-ux-toggle-single"
    },
    isAtBreakpoint: function( bpString ) {
        /* This function tests to see the current breakpoint exists in the
            allowed bp strings provided as input */
        var atBreakpoint = true;
        // If the breakpoint string exists and is not empty
        if ( typeof bpString !== "undefined" && bpString !== "" ) {
            // Test that our current breakpoint is in this list of support breakpoints
            var bps = bpString.split( " " );
            atBreakpoint = false;
            // If the first array value is not empty
            $.each( bps, function( idx, bp ) {
                // Check that the bp value is one of the supported breakpoints
                if ( $.inArray( bp, [ "xxs", "xs", "sm", "md", "lg" ] ) >= 0 ) {
                    if ( rh.webux.utils[ bp ]() ) {
                        atBreakpoint = true;
                    }
                }
            } );
        }
        return atBreakpoint;
    },
    getTrigger: function( $target ) {
        var toggleID, $trigger;
        if ( typeof $target !== "undefined" ) {
            toggleID = $target.attr( "id" );
            if ( typeof toggleID !== "undefined" && toggleID !== "" ) {
                $trigger = $( "[" + this.attr.toggleID + "=" + toggleID + "]", context );
            } else {
                $trigger = $target.prev();
            }
        }
        return $trigger;
    },
    getTarget: function( $trigger ) {
        var toggleID, $target;
        if ( typeof $trigger !== "undefined" ) {
            toggleID = $trigger.attr( this.attr.toggleID );
            if ( typeof toggleID !== "undefined" && toggleID !== "" ) {
                $target = $( "#" + toggleID, context );
            } else {
                $target = $trigger.siblings( "[" + this.attr.toggleTarget + "]" );
            }
        }
        return $target;
    },
    reveal: function( $trigger ) {
        var that = this,
            action = false;
        this.getTarget( $trigger ).each( function( idx, el ) {
            // Update state attributes on target and trigger elements
            $( el ).slideDown( "fast" ).attr( that.attr.state, "open" );
            $trigger.attr( that.attr.state, "open" );
            // If the trigger has an alignment attribute, call the align function
            if ( ( typeof $trigger.attr( that.attr.align ) !== "undefined" &&
                    $trigger.attr( that.attr.align ) !== "" ) ||
                $trigger.has( "[" + that.attr.align + "]" ).length > 0 ) {
                rh.webux.alignElement.init( $trigger );
            }
            action = true;
        } );
        return action;
    },
    hide: function( $trigger ) {
        var that = this,
            action = false,
            $target;
        this.getTarget( $trigger ).each( function( idx, el ) {
            $target = $( el );
            if ( $target.attr( that.attr.state ) !== "closed" ) {
                // Update state attributes on target and trigger elements
                $target.slideUp( "fast" ).attr( that.attr.state, "closed" );
                $trigger.attr( that.attr.state, "closed" );
                action = true;
            }
        } );
        return action;
    },
    element: function( $trigger, change ) {
        var that = this,
            bpTargets,
            state, action = false;
        this.getTarget( $trigger ).each( function( idx, el ) {
            state = $trigger.attr( that.attr.state );
            bpTargets = $( el ).attr( that.attr.toggleTarget );
            // If we are approved to toggle
            if ( that.isAtBreakpoint( bpTargets ) || bpTargets === "" ) {
                /* If the state is closed, an empty string,
                or the attribute does not exist, slide down */
                if ( state === "closed" || state === "" ||
                    state === "undefined" || typeof state === "undefined" ) {
                    if ( change ) {
                        action = that.reveal( $trigger ) ? true : action;
                    } else {
                        action = that.hide( $trigger ) ? true : action;
                    }
                } else {
                    if ( change ) {
                        action = that.hide( $trigger ) ? true : action;
                    } else {
                        action = that.reveal( $trigger ) ? true : action;
                    }
                }
            }
        } );
        return action;
    },
    reset: function( $trigger ) {
        var that = this,
            targetBps;
        this.getTarget( $trigger ).each( function( idx, el ) {
            targetBps = $( el ).attr( that.attr.toggleTarget );
            // If not an allowed breakpoint, remove attribute settings
            if ( that.isAtBreakpoint( targetBps ) || targetBps === "" ) {
                that.element( $trigger, false );
            } else if ( $( el ).attr( that.attr.state ) === "closed" ) {
                that.reveal( $trigger );
            }
        } );
    },
    event: function( $trigger, singleTrigger ) {
        var that = this,
            delay = 0,
            hide;
        if ( singleTrigger ) {
            hide = false;
            // Hide sibling elements
            $trigger.siblings( "[" + that.attr.openEvent + "]" ).each( function( idx, el ) {
                hide = that.hide( $( el ) ) ? true : hide;
            } );
            // If any of the siblings have been hidden, add a delay to the UI
            if ( hide ) {
                delay = 200;
            }
            setTimeout( function() {
                that.element( $trigger, true );
            }, delay );
        } else {
            that.element( $trigger, true );
        }
    }
};

/* On load, trigger the closing of any open accordions
    that have a state of closed set and attach click event */

$( "[" + rh.webux.toggle.attr.openEvent + "=hover], [" + rh.webux.toggle.attr.openEvent + "=click]",
    context ).each( function( idx, val ) {
    var $val = $( val ),
        toggleID = $val.attr( rh.webux.toggle.attr.toggleID ),
        eventType = $val.attr( rh.webux.toggle.attr.openEvent ),
        singleTrigger = false;

    // Find out if this element is wrapped in a single toggle attribute
    if ( $val.closest( "[" + rh.webux.toggle.attr.toggleSingle + "]" ).length > 0 ) {
        singleTrigger = true;
    }

    // OnLoad activate current state based on data attribute
    rh.webux.toggle.element( $val, false );
    if ( eventType === "click" ) {
        // OnClick change current state and data attribute
        $val.click( function() {
            rh.webux.toggle.event( $val, singleTrigger );
        } );
    } else if ( eventType === "hover" ) {
        // OnHover change current state and data attribute
        $val.hover( function() {
            rh.webux.toggle.event( $val, singleTrigger );
        } );
    }
} );

/* Trigger the closing by clicking inside the target element */
$( "[" + rh.webux.toggle.attr.closeEvent + "=click]", context ).each( function( idx, val ) {
    var $val = $( val ),
        toggleID = $val.attr( rh.webux.toggle.attr.toggleID );
    // OnClick change current state and data attribute
    rh.webux.toggle.getTarget( $val ).click( function() {
        rh.webux.toggle.element( $val, true );
    } );
} );

// Create debounce function to only trigger calls one time after it finishes resizing,
// instead of hundreds of times while it is updated
$( window ).on( "resize", function() {
    clearTimeout( resizeTimer );
    var that = rh.webux.toggle,
        resizeTimer = setTimeout( function() {
            // Reset any element that have been triggered before resizing and need to be reset.
            $( "[" + that.attr.openEvent + "=hover],[" + that.attr.openEvent + "=click]", context )
                .each( function( idx, val ) {
                    var $val = $( val ),
                        toggleID = $val.attr( that.attr.toggleID );
                    if ( typeof toggleID !== "undefined" && toggleID !== "" ) {
                        that.reset( $val );
                    } else {
                        that.reset( $val );
                    }
                } );
        }, 200 );
} );

/**
* Manages the accordion open/closed states
*
* Object literal containing 1 function: click(), which takes 2 inputs:
* - the element clicked
* - a string of the class name for the content container
*/

// Include the helpers tools so that the toggleString function is available
// -- Global helper functions

// Attach a string toggle function for use on elements
if ( typeof String.prototype.toggleString == "undefined" ) {
    String.prototype.toggleString = function( string1, string2 ) {
        return ( String( this ) === string1 ) ? string2 : string1;
    };
}

// CUSTOM JS to convert query string to JSON obj
if ( typeof rh.webux.queryToJSON == "undefined" ) {
    rh.webux.queryToJSON = function( input ) {
        var sets = {},
            search = ( typeof input === "undefined" ) ? location.search : input,
            array = ( typeof search != "undefined" ) ? search.slice( 1 ).split( "&" ) : [ "" ];
        $.each( array.filter( function( n ) {
            return n !== "";
        } ), function( idx, val ) {
            var group = val.split( "=" );
            sets[ group[ 0 ] ] = decodeURIComponent( group[ 1 ] || "" );
        } );
        return JSON.parse( JSON.stringify( sets ) );
    };
}

// Supports tab interactions and direct linking to tabset
if ( typeof rh.webux.hash == "undefined" ) {
    rh.webux.hash = function( location ) {
        // Default to window.location
        location = location || window.location;
        return location.hash.replace( /^#/, "" ).split( "." );
    };
}

// Get the height of a hidden or potentially hidden element
if ( typeof rh.webux.getTrueHeight == "undefined" ) {
    rh.webux.getTrueHeight = function( $el ) {
        // Copy element, hide, set height auto and copy current width
        var $copy = $el.clone().css( {
                "display": "block",
                "position": "absolute",
                "top": "-999px",
                "left": "-999px",
                "height": "auto",
                "minHeight": "auto",
                "maxHeight": "auto",
                "width": $el.outerWidth() + "px"
            } ).appendTo( "body" ),
            // Get the height of the clone element
            height = $copy.outerHeight();
        // Remove the cloned element
        $copy.remove();
        return height;
    };
}


rh.webux.accordion = {
    click: function( el, content ) {
        var $parent = $( el ),
            state = $parent.attr( "data-ux-state" ) || "";
        // Toggle content area open or closed
        $parent.children( content ).slideToggle( ".3s" );
        // Update the state
        $parent.attr(
            "data-ux-state",
            state.toggleString( "open", "closed" )
        );
    }
};

/**
* Progressive enhancement approach:
* For each accordion item that is not already open,
* find the content for that item and hide it
*/
$( ".ux-accordion-item:not([data-ux-state=open])", context ).each( function( key, val ) {
    $( val ).find( ".ux-accordion-content" ).hide();
} );

/**
* Attach the click function to the
* onclick event for the accordion item
*/
$.each( rh.webux.accordion, function( evt, func ) {
    $( ".ux-accordion-item", context ).on( evt, function() {
        func( this, ".ux-accordion-content" );
    } );
} );

// -- Global helper functions

// Attach a string toggle function for use on elements
if ( typeof String.prototype.toggleString == "undefined" ) {
    String.prototype.toggleString = function( string1, string2 ) {
        return ( String( this ) === string1 ) ? string2 : string1;
    };
}

// CUSTOM JS to convert query string to JSON obj
if ( typeof rh.webux.queryToJSON == "undefined" ) {
    rh.webux.queryToJSON = function( input ) {
        var sets = {},
            search = ( typeof input === "undefined" ) ? location.search : input,
            array = ( typeof search != "undefined" ) ? search.slice( 1 ).split( "&" ) : [ "" ];
        $.each( array.filter( function( n ) {
            return n !== "";
        } ), function( idx, val ) {
            var group = val.split( "=" );
            sets[ group[ 0 ] ] = decodeURIComponent( group[ 1 ] || "" );
        } );
        return JSON.parse( JSON.stringify( sets ) );
    };
}

// Supports tab interactions and direct linking to tabset
if ( typeof rh.webux.hash == "undefined" ) {
    rh.webux.hash = function( location ) {
        // Default to window.location
        location = location || window.location;
        return location.hash.replace( /^#/, "" ).split( "." );
    };
}

// Get the height of a hidden or potentially hidden element
if ( typeof rh.webux.getTrueHeight == "undefined" ) {
    rh.webux.getTrueHeight = function( $el ) {
        // Copy element, hide, set height auto and copy current width
        var $copy = $el.clone().css( {
                "display": "block",
                "position": "absolute",
                "top": "-999px",
                "left": "-999px",
                "height": "auto",
                "minHeight": "auto",
                "maxHeight": "auto",
                "width": $el.outerWidth() + "px"
            } ).appendTo( "body" ),
            // Get the height of the clone element
            height = $copy.outerHeight();
        // Remove the cloned element
        $copy.remove();
        return height;
    };
}


rh.webux.videoBand = {
    // This is set on initialize
    $parent: undefined,
    attr:   {
        state: [ "visible", "hidden" ],
        action: [ "video-play", "video-close" ]
    },
    getContext: function( context ) {
        return $( ".ux-video-embed", context );
    },
    getContainers: function() {
        return $( ".ux-video-embed-details", this.$parent );
    },
    togglePlay:   function( $iframe, state ) {
        // Create temp object to get query string
        var temp = document.createElement( "a" );
        temp.href = $iframe.attr( "src" );
        var urlJSON  = rh.webux.queryToJSON( temp.search ),
            autoplay = urlJSON.autoplay.toggleString( "0", "1" );

        $iframe.attr( "src", $iframe.attr( "src" ).replace( /autoplay=[0|1]/, "autoplay=" + autoplay ) );
    },
    whichAnimationEvent: function() {
        var a,
            el = document.createElement( "temp" ),
            animations = {
                // Standard syntax
                "animation": "animationend",
                // Chrome, Safari, Opera
                "WebkitAnimation": "webkitAnimationEnd"
            };
        // Return the animation state that's supported
        for ( a in animations ) {
            if ( el.style[ a ] !== undefined ) {
                return animations[ a ];
            }
        }
    },
    toggleState: function( el ) {
        var myself = this,
            $el = $( el ),
            current = $el.attr( "data-ux-state" );
        $el.attr(
            "data-ux-state",
            current.toggleString(
                myself.attr.state[ 0 ],
                myself.attr.state[ 1 ]
            )
        );
    },
    switchVisible: function( $el ) {
        var myself = this;
        $el.each( function( idx, val ) {
            myself.toggleState( val );
        } );
    },
    addAnimationListener:   function( el, func ) {
        var animationEvt = this.whichAnimationEvent();
        // Get correct animation end event
        if ( animationEvt ) {
            $( el ).on( animationEvt, func );
        }
    },
    init: function( context ) {
        this.$parent = this.getContext( context );
        var myself = this,
            $el = this.getContainers();

        // Add event listener to elements
        $el.each( function( idx, val ) {
            myself.addAnimationListener( val, function( evt ) {
                myself.animateComplete( val, evt );
            } );
        } );
    },
    animateComplete:    function( el, evt ) {
        var $target      = $( evt.currentTarget ),
            $el          = $( el ),
            visibleState = $target.data( "ux-state" ),
            animation    = $target.data( "ux-animation" ),
            playState    = ( visibleState == "hidden" && animation == "video-play" ),
            closeState   = ( visibleState == "hidden" && animation == "video-close" );

        if ( playState ) {
            $el.attr( "data-ux-animation", "done" )
                .siblings().attr( "data-ux-animation", "done" );
            this.toggleState( el );
            this.toggleState( $el.siblings() );
            this.togglePlay(
                $( ".ux-video-embed-iframe-container iframe", $el ),
                animation
            );
        }
    },
    animate:   function( el, state ) {
        $( el ).attr( "data-ux-animation", state );
    },
    triggerAnimation: function( action ) {
        var myself = this,
            $el    = this.getContainers();
        $el.each( function( idx, val ) {
            myself.animate( val, action );
        } );
    }
};

// Progressive enhancement approach
rh.webux.videoBand.init( context );

// On click event fires
$( ".ux-cta-link[data-ux-action]", context ).on( "click", function( e ) {
    e.preventDefault();
    rh.webux.videoBand.triggerAnimation( $( this ).attr( "data-ux-action" ) );
} );

// Theme Toggle for Cards
rh.webux.themeToggle = function( target, type, event ) {
    var $target = $( target ),
        attr     = "data-ux-" + type,
        selector = "data-ux-" + type + "-" + event,
        currentType = $target.attr( attr ),
        newType = $target.attr( selector );

    $target.attr( attr, newType );
    $target.attr( selector, currentType );
};

$.each( [ "theme", "background" ], function( idx, type ) {
    $( "[data-ux-" + type + "-hover]", context ).hover( function() {
        rh.webux.themeToggle( this, type, "hover" );
    } );

    $( "[data-ux-" + type + "-click]", context ).click( function() {
        rh.webux.themeToggle( this, type, "click" );
    } );
} );

        }
    };
} )( window, jQuery, ( "undefined" == typeof rh ) ? {} : rh );
