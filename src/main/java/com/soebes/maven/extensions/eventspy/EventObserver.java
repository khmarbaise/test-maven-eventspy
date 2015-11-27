package com.soebes.maven.extensions.eventspy;

import java.util.List;

import javax.inject.Named;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.DependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Karl Heinz Marbaise <khmarbaise@apache.org>
 */
@Named
public class EventObserver
    extends AbstractEventSpy
{

    private final Logger LOGGER = LoggerFactory.getLogger( getClass() );

    private final String failure = System.getProperty( "failure" );

    public EventObserver()
    {
        LOGGER.info( "**************************" );
        LOGGER.info( "EventObserver ctor called." );
        LOGGER.info( "**************************" );
    }

    private boolean hasFailure()
    {
        boolean result = false;
        if ( failure != null )
        {
            result = true;
        }
        return result;
    }

    @Override
    public void init( Context context )
        throws Exception
    {
        LOGGER.info( "EventObserver::init() EventSpy Hi there." );
        LOGGER.info( "EventObserver::init() failure={}.", failure );

    }

    /**
     * Notifies the spy of some build event/operation.
     * 
     * @param event
     *            The event, never {@@code null}.
     * @see org.apache.maven.settings.building.SettingsBuildingRequest
     * @see org.apache.maven.settings.building.SettingsBuildingResult
     * @see org.apache.maven.execution.MavenExecutionRequest
     * @see org.apache.maven.execution.MavenExecutionResult
     * @see org.apache.maven.project.DependencyResolutionRequest
     * @see org.apache.maven.project.DependencyResolutionResult
     * @see org.apache.maven.execution.ExecutionEvent
     * @see org.sonatype.aether.RepositoryEvent
     */
    @Override
    public void onEvent( Object event )
        throws Exception
    {
        try
        {
            if ( event instanceof ExecutionEvent )
            {
                executionEventHandler( (ExecutionEvent) event );
            }
            else if ( event instanceof SettingsBuildingRequest )
            {
                settingsBuilderRequestEvent( (SettingsBuildingRequest) event );
            }
            else if ( event instanceof SettingsBuildingResult )
            {
                settingsBuilderResultEvent( (SettingsBuildingResult) event );
            }
            else if ( event instanceof MavenExecutionRequest )
            {
                executionRequestEventHandler( (MavenExecutionRequest) event );
            }
            else if ( event instanceof MavenExecutionResult )
            {
                executionResultEventHandler( (MavenExecutionResult) event );
            }
            else if ( event instanceof DependencyResolutionRequest )
            {
                dependencyResolutionRequestEvent( (DependencyResolutionRequest) event );
            }
            else if ( event instanceof DependencyResolutionResult )
            {
                dependencyResolutionResultEvent( (DependencyResolutionResult) event );
            }
//            else if ( event instanceof RepositoryEvent )
//            {
//                repositoryEvent( (RepositoryEvent) event );
//            }
        }
        catch ( Exception e )
        {
            LOGGER.error( "Exception", e );
        }
    }

    @Override
    public void close()
    {
        LOGGER.info( "EventObserver: Bye bye." );
    }

//    private void repositoryEvent( RepositoryEvent event )
//    {
//        LOGGER.info( "EventObserver::repositoryEvent", event );
//    }

    private void dependencyResolutionRequestEvent( DependencyResolutionRequest event )
    {
        LOGGER.info( "EventObserver::dependencyResolutionRequestEvent", event );
    }

    private void dependencyResolutionResultEvent( DependencyResolutionResult event )
    {
        LOGGER.info( "EventObserver::dependencyResolutionResultEvent", event );
    }

    private void settingsBuilderRequestEvent( SettingsBuildingRequest event )
    {
        LOGGER.info( "EventObserver::settingsBuilderEvent", event );
    }

    private void settingsBuilderResultEvent( SettingsBuildingResult event )
    {
        LOGGER.info( "EventObserver::settingsBuilderResultEvent", event );
    }

    private void executionResultEventHandler( MavenExecutionResult event )
    {
        LOGGER.info( "EventObserver::executionResultEventHandler", event );
        List<MavenProject> topologicallySortedProjects = event.getTopologicallySortedProjects();
        for ( MavenProject mavenProject : topologicallySortedProjects )
        {
            LOGGER.info( " Project:" + mavenProject.getId() );
            List<Dependency> dependencies = mavenProject.getDependencies();
            for ( Dependency dependency : dependencies )
            {
                LOGGER.info( "      -> " + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":"
                    + dependency.getVersion() );
            }
        }
    }

    private void executionRequestEventHandler( MavenExecutionRequest event )
    {
        LOGGER.info( "EventObserver::executionRequestEventHandler", event );
    }

    private void executionEventHandler( ExecutionEvent executionEvent )
    {
        switch ( executionEvent.getType() )
        {
            case SessionEnded:
                LOGGER.info( "EventObserver::executionEventHandler({}) {}", executionEvent.getType().name(),
                             executionEvent );
                if ( hasFailure() )
                {
                    if ( failure.equalsIgnoreCase( "true" ) )
                    {
                        MavenExecutionResult result = executionEvent.getSession().getResult();
                        result.addException( new MojoFailureException( "failed on purpose" ) );
                    }
                }
                break;
            case ForkFailed:
            case ForkStarted:
            case ForkSucceeded:
            case ForkedProjectFailed:
            case ForkedProjectStarted:
            case ForkedProjectSucceeded:
            case MojoFailed:
            case MojoSkipped:
            case MojoStarted:
            case MojoSucceeded:
            case ProjectDiscoveryStarted:
            case ProjectFailed:
            case ProjectSkipped:
            case ProjectStarted:
            case ProjectSucceeded:
            case SessionStarted:
                LOGGER.info( "EventObserver::executionEventHandler({}) {}", executionEvent.getType().name(),
                             executionEvent );
                break;
            default:
                LOGGER.info( "EventObserver::executionEventHandler(UNKNOWN) {}", executionEvent.getType().name() );
                break;
        }
    }

}
