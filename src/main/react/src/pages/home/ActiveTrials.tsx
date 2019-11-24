import React, {Component} from "react";
import {connect} from "react-redux";
import {NavLink} from "react-router-dom";
import {AppState} from "../../store";
import {refreshTrials} from "../../store/activetrials/operations";
import {server} from "../../utils/server";
import {Trials} from "../Scale";

interface Props {
    refreshTrials: () => void;

    trials: Trials;
}

class ActiveTrials extends Component<Props> {
    constructor(props: Readonly<Props>) {
        super(props);
        this.renderActiveTrial = this.renderActiveTrial.bind(this);
        this.completeTrial = this.completeTrial.bind(this);
    }

    public componentDidMount() {
        this.props.refreshTrials();
    }

    public render() {
        return (
            <span id="activeTrials">
                <h2>Active trials</h2>
                {this.props.trials.map(this.renderActiveTrial)}
            </span>
        );
    }

    private renderActiveTrial(trial: string, idx: number) {
        return <div key={idx} className="flex-group margin-bottom-1 padding-bottom-1 border-bottom">
            <NavLink to={"/trial/" + trial}>{trial}</NavLink>
            <div className="align-right"><button className="primary" onClick={(_) => this.completeTrial(trial)}>Complete</button></div>
        </div>;
    }

    private completeTrial(trialName: string) {
        fetch(server.trial(trialName), {method: "DELETE", credentials: "same-origin"})
            .then(() => this.props.refreshTrials());
    }
}

const mapStateToProps = (state: AppState) => ({
    trials: state.activeTrials,
});

export default connect(
    mapStateToProps,
    { refreshTrials },
)(ActiveTrials);
